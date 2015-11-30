package com.example.michaelgifford.cisc434project2;

/**
 * Created by michaelgifford on 15-11-29.
 */

import java.util.ArrayList;

import com.example.michaelgifford.cisc434project2.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyCustomBaseAdapter extends BaseAdapter {
    private static ArrayList<roomResults> searchArrayList;

    private LayoutInflater mInflater;

    public MyCustomBaseAdapter(Context context, ArrayList<roomResults> results) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.customrow, null);
            holder = new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.name);
            holder.txtnumCapacity = (TextView) convertView
                    .findViewById(R.id.numCapacity);
            holder.txtnumCurrentUsers = (TextView) convertView.findViewById(R.id.numCurrentUsers);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(searchArrayList.get(position).getName());
        holder.txtnumCapacity.setText(searchArrayList.get(position)
                .getCapacity());
        holder.txtnumCurrentUsers.setText(searchArrayList.get(position).getNumCurrentUsers());

        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
        TextView txtnumCapacity;
        TextView txtnumCurrentUsers;
    }
}