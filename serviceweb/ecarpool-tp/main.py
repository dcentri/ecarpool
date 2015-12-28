# -*- coding: utf-8 -*-

import webapp2
import logging
import traceback
import datetime
import json

from google.appengine.ext import ndb
from google.appengine.ext import db
from datetime import datetime

# Importation des modèles de données personnalisés.
from models import User, Address, Trajet, Parcours, Messages
from math import radians, cos, sin, asin, sqrt

def update_parcour(object, id):
    if id is not None and id != "":
        cle = ndb.Key('Parcours', long(id))
        newParcours = cle.get()
        if newParcours is None:
            newParcours = Parcours()
    else:
        newParcours = Parcours()
    newParcours.driver = object['trajetDefault']['idAuthor']
    newParcours.nbPlaces = object['nbPlaces']
    newParcours.price = object['price']
    newParcours.km = object['km']
    idTrajet = add_trajet(object['trajetDefault'], object['trajetDefault']['id']).id()
    if idTrajet not in newParcours.trajets:
        newParcours.trajets.append(idTrajet)
    return newParcours.put()
def add_trajet(object, id):
    if id is not None and id != "":
        cle = ndb.Key('Trajet', long(id))
        newTrajet = cle.get()
        if newTrajet is None:
            newTrajet = Trajet()
    else:
        newTrajet = Trajet()

    newTrajet.idAuthor = object['idAuthor']
    if('booked' in object):
        newTrajet.booked = object['booked']
    newTrajet.departureDateTime = datetime.strptime(object['departureDateTime'], '%b %d %Y %I:%M%p')
    newTrajet.arrivalDateTime = datetime.strptime(object['arrivalDateTime'], '%b %d %Y %I:%M%p')
    newTrajet.frequency = object['frequency']
    newTrajet.departureAddress = add_address(object['departureAddress'], object['departureAddress']["id"]).id()
    newTrajet.arrivalAddress = add_address(object['arrivalAddress'], object['arrivalAddress']["id"]).id()
    return newTrajet.put()

def add_address(object, id):
    """
    Ajout une address dans la base de données et retourne la clé de l'adresse    """
    if id is not None and id != "":
        cle = ndb.Key('Address', long(id))
        newUserAddress = cle.get()
        if newUserAddress is None:
            newUserAddress = Address()
    else:
        newUserAddress = Address()
    
    newUserAddress.civicNo = object['civicNo']
    newUserAddress.routeName = object['routeName']
    newUserAddress.postalCode = object['postalCode']
    newUserAddress.appartNo = object['appartNo']
    newUserAddress.long = object['long']
    newUserAddress.lat = object['lat']
    return newUserAddress.put()

    newUser.idAddress = newUserAddress.put().id()
def serialiser_pour_json(objet):
    """ Permet de sérialiser les dates et heures pour transformer
        un objet en JSON.

        Args:
            objet (obj): L'objet à sérialiser.

        Returns:
            obj : Si c'est une date et heure, retourne une version sérialisée
                  selon le format ISO (str); autrement, retourne l'objet
                  original non modifié.
        """
    if isinstance(objet, datetime):
        # Pour une date et heure, on retourne une chaîne
        # au format ISO (sans les millisecondes).
        return objet.replace(microsecond=0).isoformat()
    elif isinstance(objet, date):
        # Pour une date, on retourne une chaîne au format ISO.
        return objet.isoformat()
    else:
        # Pour les autres types, on retourne l'objet tel quel.
        return objet


# mainHandler
class MainPageHandler(webapp2.RequestHandler):

    def get(self):
        # Permet de vérifier si le service Web est en fonction.
        # On pourrait utiliser cette page pour afficher de l'information
        # (au format HTML) sur le service Web REST.
        self.response.headers['Content-Type'] = 'text/plain; charset=utf-8'
        self.response.out.write('Sercice Web ecarpool ' +
                                'Google App Engine" en fonction !!!')


# ConnexionHandler
class ConnexionHandler(webapp2.RequestHandler):

    def post(self):
        """ Permet de valider une connexion sur le réseau sociaux.
        Deux variable sont necessaire: login et password
        le format du json pour le login sembles à {"login":"albert", "password":"mp"}"""
        try:
            # Récuperation d'information json du login.
            dude_dict_in = json.loads(self.request.body)
            cle = ndb.Key('User', dude_dict_in['login'])

            # Récuperation de l'user s'il existe
            dude = cle.get()

            # Si l'user n'existe pas retourner une erruer
            if dude is None or dude.password != dude_dict_in['password']:
                status = 400
                dude_dict_out = {}
                dude_dict_out['erreur'] = "Combinaison login, mot de passe incorrect."
            else:
                status = 201
                dude_dict_out = dude.to_dict()
                dude_dict_out['login'] = dude_dict_in['login']
                self.response.headers['Location'] = (self.request.url + '/' +
                                                     str(dude_dict_in['login']))

            # Configuration du code de statut HTTP (201 Created).
            self.response.set_status(status)

            # Le corps de la réponse contiendra une représentation en JSON
            # de l'animal qui vient d'être créé.
            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')

            dude_json = json.dumps(dude_dict_out, default=serialiser_pour_json)
            self.response.out.write(dude_json)

        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

# userHandler
class UserHandler(webapp2.RequestHandler):

    def put(self, login):
        """Permet de créer un user sur le reseaux
        le json nécéssaire pour la création est 
        {
             "login":"alb", 
             "password":"mp", 
             "firstName":"Albert",
             "lastName":"Bitreyeson", 
            "userType":"Driver",
             "gender":"Homme", 
             "phone":"418-6884160", 
             "email":"albertb@gmail.com",
            "userAddress":
            {
                "civicNo":"2315", 
                "routeName":"Boul. Legendre", 
                "postalCode":"G1P 2X2", 
                "appartNo":"", 
                "long":"13213212332132", 
                "lat":"132132132132132213"
            }
        }
        """
        try:
            cle = ndb.Key('User', login)
            newUser = cle.get()

            if newUser is None:
                status = 201
                addressId = ""
                newUser = User(key=cle)
            else:
                addressId = newUser.idAddress;
                status=200

            user_dict_in = json.loads(self.request.body)
            newUser.password = user_dict_in['password']
            newUser.email = user_dict_in['email']
            newUser.gender = user_dict_in['gender']
            newUser.firstName = user_dict_in['firstName']
            newUser.lastName = user_dict_in['lastName']
            newUser.userType = user_dict_in['userType']
            newUser.phone = user_dict_in['phone']

            newUser.idAddress = add_address(user_dict_in['userAddress'], addressId).id()

            cle_user = newUser.put()
            self.response.set_status(status)

            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')
            user_dict = newUser.to_dict()
            user_dict['login'] = cle_user.id()
            json_data = json.dumps(user_dict, default=serialiser_pour_json)
            self.response.out.write(json_data)

        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

    def get(self, login):
        try:
            cle = ndb.Key('User', login)
            user = cle.get()
            if user is None:
                self.error(404)
                return
            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')
            user_dict = user.to_dict()
            user_dict['login'] = cle.id()
            json_data = json.dumps(user_dict, default=serialiser_pour_json)
            self.response.out.write(json_data)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

# trajetHandler
class AddressHandler(webapp2.RequestHandler):

    def get(self, id):
        try:
            cle = ndb.Key('Address', long(id))
            address = cle.get()
            if address is None:
                self.error(404)
                return
            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')
            address_dict = address.to_dict()
            address_dict['id'] = cle.id()
            json_data = json.dumps(address_dict, default=serialiser_pour_json)
            self.response.out.write(json_data)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

class UserParcoursHandler(webapp2.RequestHandler):
    def get(self, idUser):
        try:
            if(idUser is not None):
                cle = ndb.Key('User', idUser)
                dude = cle.get()
                if(dude is None):
                    self.error(400)
                    return
                liste_parcours = []
                requete = Parcours.query()
                requete = requete.filter(Parcours.driver == idUser)
                for prc in requete:
                    prc_dict = prc.to_dict()
                    prc_dict['id'] = prc.key.id()
                    liste_parcours.append(prc_dict)
                json_data = json.dumps(liste_parcours, default=serialiser_pour_json)
            else:
                self.error(400)
                return
            self.response.out.write(json_data)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

class UserTrajetsHandler(webapp2.RequestHandler):
    def get(self, idUser):
        try:
            if(idUser is not None):
                cle = ndb.Key('User', idUser)
                dude = cle.get()
                if(dude is None):
                    self.error(400)
                    return
                liste_trajets = []
                requete = Trajet.query()
                requete = requete.filter(Trajet.idAuthor == idUser)
                for trj in requete:
                    trj_dict = trj.to_dict()
                    trj_dict['id'] = trj.key.id()
                    liste_trajets.append(trj_dict)
                json_data = json.dumps(liste_trajets, default=serialiser_pour_json)
            else:
                self.error(400)
                return
            
            self.response.out.write(json_data)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

class ParcoursHandler(webapp2.RequestHandler):
    def post(self):
        """Permet d'ajouter un nouveau parcours à la liste des parcours disponibles
        {
          "nbPlaces" : 4,
          "price" : 22.00,
          "km" : 5,
          "trajetDefault" :
          {    
          "id":"",
          "idAuthor":"alb",
          "departureDateTime":"Jun 1 2005  1:33PM", 
          "arrivalDateTime":"Jun 1 2005  2:33PM", 
          "frequency":"mp" ,
          "departureAddress":
            {
            "id":"", "civicNo":"2315", "routeName":"Boul Lemieux", "postalCode":"G1P 5X5", "appartNo":"11"
            , "long":"3.3852100", "lat":"47.3590900"
            }, 
          "arrivalAddress":
            {
            "id":"", "civicNo":"4562", "routeName":"Boul Legendre", "postalCode":"G1P 5X5", "appartNo":"18"
            , "long":"-71.3330222", "lat":"46.8208264"
            }
          }
        }
        """
        try:
            # Récuperation d'information json du login.
            parcours_dict_in = json.loads(self.request.body)
            cle = ndb.Key('User', parcours_dict_in['trajetDefault']['idAuthor'])

            # Récuperation de l'user s'il existe
            dude = cle.get()
            if dude is None:
                self.error(400)
                return
            cle_parcour = update_parcour(parcours_dict_in, None)
            
            self.response.set_status(200)

            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')
            parcour_dict = cle_parcour.get().to_dict()
            parcour_dict['id'] = cle_parcour.id()
            json_data = json.dumps(parcour_dict, default=serialiser_pour_json)
            self.response.out.write(json_data)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)
    def put(self, id):
        try:
            cle = ndb.Key('Parcours', long(id))
            parcour = cle.get()
            if parcour is None:
                self.error(400)
                return
            parcours_dict_in = json.loads(self.request.body)
            cle = update_parcour(parcours_dict_in, id)
            self.response.set_status(200)

            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')
            parcour_dict = cle.get().to_dict()
            parcour_dict['id'] = cle.id()
            json_data = json.dumps(parcour_dict, default=serialiser_pour_json)
            self.response.out.write(json_data)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

    def get(self, id=None):
        try:
            if(id is not None):
                cle = ndb.Key('Parcours', long(id))
                parcour = cle.get()
                if parcour is None:
                    self.error(404)
                    return
                parcour_dict = parcour.to_dict()
                parcour_dict['id'] = cle.id()
                json_data = json.dumps(parcour_dict, default=serialiser_pour_json)
            else:
                liste_parcours = []
                requete = Parcours.query()
                for prc in requete:
                    prc_dict = prc.to_dict()
                    prc_dict['id'] = prc.key.id()
                    liste_parcours.append(prc_dict)
                json_data = json.dumps(liste_parcours, default=serialiser_pour_json)
            self.response.out.write(json_data)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

    def delete(self, id):
        try:
            cle = ndb.Key('Parcours', long(id))
            parcour = cle.get()

            if parcour is None:
                self.error(400)
                return
            parcour.key.delete()
            self.response.set_status(204)

        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

class ParcoursTrajetHandler(webapp2.RequestHandler):

    def put(self, login, idParcours, idTrajet):
        try:
            # Méthode pour les passagers qui veulent ajouter leur trajet à un parcours''''
            cle_user_initiator = ndb.Key('User', login)
            cle_parcours = ndb.Key('Parcours', long(idParcours))
            cle_trajet = ndb.Key('Trajet', long(idTrajet))
            user_initiator = cle_user_initiator.get()
            parcours = cle_parcours.get()
            trajet = cle_trajet.get()
            if user_initiator is None or parcours is None or trajet is None:
                self.error(400)
                return
            cle_user_target = ndb.Key('User', parcours.driver)
            user_target = cle_user_target.get()
            msg_dict_out = {}
            if idParcours not in user_initiator.listDemandesParcours and idTrajet not in user_target.listeDemandesTrajet and not trajet.booked:
                user_target.listeDemandesTrajet.append(idTrajet)
                msg = Messages()
                msg.sender = login
                msg.to = parcours.driver;
                msg.message = user_initiator.firstName + u' vous a envoyé une demande de covoiturage'
                msg.refParcour = long(idParcours)
                msg.refTrajet = long(idTrajet)
                cle = msg.put()
                user_target.put()
                msg_dict_out['succes'] = "La demande de covoiturage a été envoyé avec succès."
            elif idParcours in user_initiator.listDemandesParcours and len(parcours.trajets) < parcours.nbPlaces and idTrajet not in parcours.trajets and not trajet.booked:
                parcours.trajets.append(long(idTrajet))
                user_initiator.listDemandesParcours.remove(idParcours)
                trajet.booked = True;
                msg = Messages()
                msg.sender = login
                msg.to = parcours.driver;
                msg.message = user_initiator.firstName + u' a accepté votre demande de covoiturage'
                msg.refParcour = long(idParcours)
                msg.refTrajet = long(idTrajet)
                cle = msg.put()
                user_target.put()
                parcours.put()
                trajet.put()
                user_initiator.put()
                self.response.set_status(200)
                msg_dict_out['succes'] = "Le trajet a été ajouté avec succès"
            elif len(parcours.trajets) >= parcours.nbPlaces or trajet.booked:
                if idParcours in user_initiator.listDemandesParcours:
                    user_initiator.listDemandesParcours.remove(idParcours)
                    user_initiator.put()
                    msg_dict_out['erreur'] = "Le parcours est déjà complet"
                else:
                    msg_dict_out['erreur'] = "Impossible de compléter votre requête."
            else:
                msg_dict_out['erreur'] = "Impossible de compléter votre requête.Votre demande est déjà envoyé ou la requête manque des données"
                self.response.set_status(400)
            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')

            response_json = json.dumps(msg_dict_out, default=serialiser_pour_json)
            self.response.out.write(response_json)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

    def delete(self, login, idParcours, idTrajet):
        try:
            cle_user_initiator = ndb.Key('User', login)
            cle_parcours = ndb.Key('Parcours', long(idParcours))
            cle_trajet = ndb.Key('Trajet', long(idTrajet))
            user_initiator = cle_user_initiator.get()
            parcours = cle_parcours.get()
            trajet = cle_trajet.get()
            if user_initiator is None or parcours is None or trajet is None:
                self.error(400)
                return

            if idParcours in user_initiator.listDemandesParcours:
                user_initiator.listDemandesParcours.remove(idParcours)
                user_initiator.put()
                msg_dict_out['succes'] = "La démande a été annulé avec succès"
            else:
                msg_dict_out['erreur'] = "Impossible de compléter votre requête."
            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')

            response_json = json.dumps(msg_dict_out, default=serialiser_pour_json)
            self.response.out.write(response_json)
            self.response.set_status(204)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

class TrajetParcoursHandler(webapp2.RequestHandler):
#Classe pour les conducteur qui veulent ajouter un trajet à leur parcours
    def put(self, login, idParcours, idTrajet):
        try:
            cle_user_initiator = ndb.Key('User', login)
            cle_parcours = ndb.Key('Parcours', long(idParcours))
            cle_trajet = ndb.Key('Trajet', long(idTrajet))
            user_initiator = cle_user_initiator.get()
            parcours = cle_parcours.get()
            trajet = cle_trajet.get()
            if user_initiator is None or parcours is None or trajet is None:
                self.error(400)
                return
            cle_user_target = ndb.Key('User', trajet.idAuthor)
            user_target = cle_user_target.get();
            msg_dict_out = {}
            ''' Ajouter une nouvelle demande'''
            if idTrajet not in user_initiator.listeDemandesTrajet and idParcours not in user_target.listDemandesParcours and not trajet.booked:
                user_target.listDemandesParcours.append(idParcours)
                msg = Messages()
                msg.sender = login
                msg.to = trajet.idAuthor;
                msg.message = user_initiator.firstName + u' vous a envoyé une demande de covoiturage'
                msg.refParcour = long(idParcours)
                msg.refTrajet = long(idTrajet)
                cle = msg.put()
                user_target.put()
                msg_dict_out['id'] = cle.id();
                msg_dict_out['succes'] = "La demande de covoiturage a été envoyé avec succès."
            elif idTrajet in user_initiator.listeDemandesTrajet and len(parcours.trajets) < parcours.nbPlaces and idTrajet not in parcours.trajets and not trajet.booked:
                ''' Accepter une demande et ajouté un trajet à un parcours'''
                parcours.trajets.append(long(idTrajet))
                user_initiator.listeDemandesTrajet.remove(idTrajet)
                msg = Messages()
                msg.sender = login
                msg.to = trajet.idAuthor;
                msg.message = user_initiator.firstName + u' a accepté votre demande de covoiturage'
                msg.refParcour = long(idParcours)
                msg.refTrajet = long(idTrajet)
                cle = msg.put()
                trajet.booked = True;
                parcours.put()
                trajet.put()
                user_initiator.put()
                self.response.set_status(200)
                msg_dict_out['succes'] = "Le trajet a été ajouté avec succès"
            elif len(parcours.trajets) >= parcours.nbPlaces or trajet.booked:
                if idTrajet in user_initiator.listeDemandesTrajet:
                    user_initiator.listeDemandesTrajet.remove(idTrajet)
                    user_initiator.put()
                    msg_dict_out['erreur'] = "Impossible d'ajouté un trajet. Le parcours est complet"
            else:
                msg_dict_out['erreur'] = "Impossible de compléter votre requête. Votre demande est déjà envoyé ou la requête manque des données"
                self.response.set_status(400)
            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')

            response_json = json.dumps(msg_dict_out, default=serialiser_pour_json)
            self.response.out.write(response_json)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

    def get(self, idtrajet):
        try:
            if(id is not None):
                liste = []
                requete = Parcours.query()
                requete = requete.filter(Parcours.trajets == int(idtrajet))
                parc = requete.get()
                if(parc is not None):
                    parc_dict = parc.to_dict()
                    parc_dict['id'] = parc.key.id()
                    liste.append(parc_dict)
                json_data = json.dumps(liste, default=serialiser_pour_json)
                self.response.headers['Content-Type'] = ('application/json;' +
                                                         ' charset=utf-8')
            else:
                self.error(400)
                return
            self.response.out.write(json_data)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

# trajetHandler
class MessageHandler(webapp2.RequestHandler):

    def post(self):
        """
        Permet d'ajouter un message à la base de données avec le format de json suivant:
        {
          "sender":"alb",
          "to":"a",
          "message":"Albert a accepté votre demande de covoiturage",
          "refParcour": 54531213213215
        }
        """
        try:
            msg_dict_in = json.loads(self.request.body)
            cle_sender = ndb.Key('User', msg_dict_in['sender'])
            cle_receiver = ndb.Key('User', msg_dict_in['to'])
            if cle_sender.get() is None or cle_receiver.get() is None:
                self.error(400)
                return
            msg = Messages()
            msg.sender = msg_dict_in['sender']
            msg.to = msg_dict_in['to']
            msg.message = msg_dict_in['message']
            msg.refParcour = msg_dict_in['refParcour']
            cle = msg.put()
            self.response.set_status(200)

            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')
            msg_dict_in['id'] = cle.id()
            json_data = json.dumps(msg_dict_in, default=serialiser_pour_json)
            self.response.out.write(json_data)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

    def get(self,login):
        """
        Retrouve toutes les messages destinées à un utilisateur qui a le login spécifié
        et retourne une liste de toutes ces messages.
        """
        try:
            cle = ndb.Key('User', login)
            destiUser = cle.get()
            if destiUser is None:
                self.error(400)
                return
            liste_msg = []
            requete = Messages.query()
            requete = requete.filter(Messages.to == login)
            for msg in requete:
                msg_dict = msg.to_dict()
                msg_dict['id'] = msg.key.id()
                liste_msg.append(msg_dict)
            json_data = json.dumps(liste_msg, default=serialiser_pour_json)
            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')
            self.response.out.write(json_data)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)
    def delete(self, login, messageId):
        try:
            cle_user = ndb.Key('User', login)
            cle = ndb.Key('Messages', long(messageId))
            msg = cle.get()
            destiUser = cle_user.get()

            if msg is None or destiUser is None or msg.to != login:
                self.error(400)
                return
            if(str(msg.refParcour) in destiUser.listDemandesParcours):
                destiUser.listDemandesParcours.remove(str(msg.refParcour))
            elif (str(msg.refTrajet) in destiUser.listeDemandesTrajet):
                destiUser.listeDemandesTrajet.remove(str(msg.refTrajet))
            destiUser.put();
            msg.key.delete()
            self.response.set_status(204)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)
# trajetHandler
class TrajetHandler(webapp2.RequestHandler):

    def post(self):
        """Permet de créer une trajet sur le serveur
        le json nécéssaire pour la création est 
        {    "idAuthor":"a",
            "departureDateTime":"mp", 
            "arrivalDateTime":"mp", 
            "frequency":"mp" ,
            "departureAddress":
            {
                "id":"", "civicNo":"mp", "routeName":"mp", "postalCode":"mp", "appartNo":"mp"
                , "long":"mp", "lat":"mp"
            }, 
            "arrivalAddress":
            {
            "id":"","civicNo":"mp", "routeName":"mp", "postalCode":"mp", "appartNo":"mp"
            , "long":"mp", "lat":"mp"
            }
        }
        """
        try:
            trajet_dict_in = json.loads(self.request.body)
            cle = ndb.Key('User', trajet_dict_in['idAuthor'])
            author = cle.get()

            if author is None:
                self.error(400)
                return

            cle_trajet = add_trajet(trajet_dict_in, None)
            self.response.set_status(201)

            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')
            trajet_dict_out = cle_trajet.get().to_dict()
            trajet_dict_out['id'] = cle_trajet.id()
            json_data = json.dumps(trajet_dict_out, default=serialiser_pour_json)
            self.response.out.write(json_data)

        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

    def delete(self, id):
        try:
            cle = ndb.Key('Trajet', id)
            trajet = cle.get()

            if trajet is None:
                self.error(400)
                return
            trajet.key.delete()
            self.response.set_status(204)

        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)
    def get(self, id=None):
        try:
            if( id is not None):
                cle = ndb.Key('Trajet', long(id))
                trajet = cle.get()
                if trajet is None:
                    self.error(404)
                    return
                self.response.headers['Content-Type'] = ('application/json;' +
                                                         ' charset=utf-8')
                trajet_dict = trajet.to_dict()
                trajet_dict['id'] = cle.id()
                json_data = json.dumps(trajet_dict, default=serialiser_pour_json)
            else:
                liste_trajets = []
                requete = Trajet.query()
                requete = requete.filter(Trajet.booked == False)
                for trj in requete:
                    trj_dict = trj.to_dict()
                    trj_dict['id'] = trj.key.id()
                    liste_trajets.append(trj_dict)
                json_data = json.dumps(liste_trajets, default=serialiser_pour_json)
            self.response.out.write(json_data)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

app = webapp2.WSGIApplication(
    [
     # créer une connexion (valider l'utilisateur
        webapp2.Route(r'/',
                      handler=MainPageHandler,
                      methods=['GET']),
        # créer une connexion (valider l'utilisateur
        webapp2.Route(r'/connexion',
                      handler=ConnexionHandler,
                      methods=['POST']),
        # créer une nouvelle utilisateur.
        webapp2.Route(r'/user/<login>',
                      handler=UserHandler,
                      methods=['PUT', 'GET']),
        # créer une nouvelle utilisateur.
        webapp2.Route(r'/address/<id>',
                      handler=AddressHandler,
                      methods=['GET']),
     # créer une nouvelle utilisateur.
        webapp2.Route(r'/parcours',
                      handler=ParcoursHandler,
                      methods=['POST','GET' ]),
     # Trouver tous les parcours d'un utilisateur.
        webapp2.Route(r'/user/<idUser>/parcours',
                      handler=UserParcoursHandler,
                      methods=['GET']),
        # créer une nouvelle utilisateur.
        webapp2.Route(r'/parcours/<id>',
                      handler=ParcoursHandler,
                      methods=['GET', 'PUT', 'DELETE']),
     # Ajouter un trajet à un parcours.
        webapp2.Route(r'/user/<login>/parcours/<idParcours>/trajet/<idTrajet>',
                      handler=TrajetParcoursHandler,
                      methods=['PUT']),
     # Ajouter un parcours à un trajet.
        webapp2.Route(r'/user/<login>/trajet/<idTrajet>/parcours/<idParcours>',
                      handler=ParcoursTrajetHandler,
                      methods=['PUT']),
     # créer une nouvelle utilisateur.
        webapp2.Route(r'/trajet/<idtrajet>/parcours',
                      handler=TrajetParcoursHandler,
                      methods=['GET']),
     # créer une nouvelle utilisateur.
        webapp2.Route(r'/trajets',
                      handler=TrajetHandler,
                      methods=['POST', 'GET']),
        # créer une nouvelle utilisateur.
        webapp2.Route(r'/trajets/<id>',
                      handler=TrajetHandler,
                      methods=['GET', 'DELETE']),
     # Trouver tous les parcours d'un utilisateur.
        webapp2.Route(r'/user/<idUser>/trajets',
                      handler=UserTrajetsHandler,
                      methods=['GET']),
        webapp2.Route(r'/message',
                      handler=MessageHandler,
                      methods=['POST']),
        webapp2.Route(r'/user/<login>/message',
                      handler=MessageHandler,
                      methods=['GET']),
        webapp2.Route(r'/user/<login>/message/<messageId>',
                      handler=MessageHandler,
                      methods=['DELETE']),
    ],
    debug=True)
