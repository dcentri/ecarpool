package com.dicentrix.ecarpool.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dicentrix.ecarpool.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Akash on 12/26/2015.
 */
public class ChoiceAdapter extends ArrayAdapter<Map<String, String>> {

    private LayoutInflater mInflater ;
    private RadioButton mSelectedRB;
    public int mSelectedPosition = -1;

    int                    mResource ;
    List< Map<String, String> > mData ;
    Context context;

    public ChoiceAdapter ( Context context , int resource , int textViewResourceId , List < Map<String, String> > data ) {
        super ( context , resource , textViewResourceId , data ) ;
        this.context = context;
        mData = data ;
        mResource = resource ;
        mInflater = ( LayoutInflater ) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }

    @ Override
    public View getView ( final int position , View convertView , ViewGroup parent ) {
        ViewHolder holder;
        holder = null;
        if ( convertView == null ) {
            convertView = mInflater.inflate ( mResource , null ) ;
            holder = new ViewHolder ( ) ;
            holder.check = (RadioButton) convertView.findViewById(R.id.rbChooseList);
            holder.text = ( TextView ) convertView.findViewById ( R.id.text ) ;
            holder.comment = ( TextView ) convertView.findViewById ( R.id.comment ) ;
            LinearLayout lin = ( LinearLayout ) convertView.findViewById ( R.id.linerList ) ;
            LinearLayout linItem = ( LinearLayout ) convertView.findViewById ( R.id.linerListItem ) ;
            ViewGroup.LayoutParams lparam = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT );
            //radioGroup.addView ( rbtn );

            convertView.setTag(holder) ;
            holder.check.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (position != mSelectedPosition && mSelectedRB != null) {
                        mSelectedRB.setChecked(false);
                    }
                    mSelectedPosition = position;
                    mSelectedRB = (RadioButton) v;
                }
            });
            linItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (position != mSelectedPosition && mSelectedRB != null) {
                        mSelectedRB.setChecked(false);
                    }

                    mSelectedPosition = position;
                    mSelectedRB = (RadioButton)v.findViewById(R.id.rbChooseList);
                    mSelectedRB.setChecked(true);
                }
            });
        } else {
            holder = ( ViewHolder ) convertView.getTag ( ) ;
        }


        holder.text.setText ( mData.get(position ).get("Heure") ) ;
        holder.comment.setText(mData.get(position).get("Depart")) ;

        return convertView ;
    }

    static class ViewHolder {
        RadioButton check;
        TextView text;
        TextView comment;
        int position;
    }
}