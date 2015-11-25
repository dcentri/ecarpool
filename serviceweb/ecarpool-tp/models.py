# -*- coding: utf-8 -*-

from google.appengine.ext import ndb


# Model concernant un site de pêche dans le service web
class Trajet(ndb.Model):
    idAuthor = ndb.StringProperty(required=True)
    departureAddress = ndb.IntegerProperty(required=True)
    arrivalAddress = ndb.IntegerProperty(required=True)
    departureDateTime = ndb.DateTimeProperty(required=False)
    arrivalDateTime = ndb.DateTimeProperty(required=False)
    frequency = ndb.StringProperty(required=True)

# Model concernant un site de pêche dans le service web
class Parcours(ndb.Model):
    driver = ndb.StringProperty(required=True)
    nbPlaces = ndb.IntegerProperty(required=True)
    price = ndb.FloatProperty(required=True)# Check type
    km = ndb.FloatProperty(required=True)# Check type
    trajets = ndb.IntegerProperty(repeated=True)

# Model concernant un site de pêche dans le service web
class Messages(ndb.Model):
    sender = ndb.StringProperty(required=True)
    to = ndb.StringProperty(required=True)# Check type
    message = ndb.StringProperty(required=True)# Check type
    refParcour = ndb.IntegerProperty(required=False)

# Model concernant un utilisateur dans le service web
class User(ndb.Model):
    password = ndb.StringProperty(required=True)
    firstName = ndb.StringProperty(required=True)
    lastName = ndb.StringProperty(required=True)
    userType = ndb.StringProperty(required=True)
    gender = ndb.StringProperty(required=True)
    phone = ndb.StringProperty(required=True)
    email = ndb.StringProperty(required=False)
    idAddress = ndb.IntegerProperty(required=True)
    listDemandesParcours = ndb.StringProperty(repeated=True, required=False)
    listeDemandesTrajet = ndb.StringProperty(repeated=True, required=False)

# Model concernant une region adminstrative dans le service web
class Address(ndb.Model):
    civicNo = ndb.StringProperty(required=True)
    routeName = ndb.StringProperty(required=True)
    postalCode = ndb.StringProperty(required=True)
    appartNo = ndb.StringProperty(required=False)
    long = ndb.StringProperty(required=True)
    lat = ndb.StringProperty(required=True)