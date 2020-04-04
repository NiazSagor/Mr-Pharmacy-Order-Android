package com.example.android.mrpharmacy_order.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.mrpharmacy_order.R;

import java.util.ArrayList;

public class OrderDetailAdapter extends ArrayAdapter<String> {
    private Context c;

    private ArrayList<String> medicineName;
    private ArrayList<String> medicineQuantity;
    private ArrayList<String> medicineBrandName;
    private ArrayList<String> medicineType;


    public OrderDetailAdapter(Context c, ArrayList<String> medicineName, ArrayList<String> medicineQuantity, ArrayList<String> medicineBrandName, ArrayList<String> medicineType) {
        super(c, R.layout.orderlistview);

        this.c = c;
        this.medicineName = medicineName;
        this.medicineQuantity = medicineQuantity;
        this.medicineBrandName = medicineBrandName;
        this.medicineType = medicineType;
    }

    static class ViewHolder {
        TextView medicineName, medicineQuantity, medicineBrandName, medicineType;
    }

    @Override
    public int getCount() {
        return medicineName.size();
    }

    @SuppressLint("ViewHolder")
    public View getView(final int position, View view, final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.orderlistview, parent, false);

        final ViewHolder holder = new ViewHolder();

        holder.medicineName = view.findViewById(R.id.medName);
        holder.medicineQuantity = view.findViewById(R.id.medQuantity);
        holder.medicineBrandName = view.findViewById(R.id.medBrand);
        holder.medicineType = view.findViewById(R.id.medType);

        //Setting the values we got from the array list, in their view position
        holder.medicineName.setText(medicineName.get(position));
        holder.medicineQuantity.setText(medicineQuantity.get(position));
        holder.medicineType.setText(medicineType.get(position));
        holder.medicineBrandName.setText(medicineBrandName.get(position));

        return view;
    }

    public ArrayList<String> getMedicineName() {
        return medicineName;
    }

    public ArrayList<String> getMedicineBrand() {
        return medicineBrandName;
    }

    public ArrayList<String> getMedicineQuantity() {
        return medicineQuantity;
    }

    public ArrayList<String> getMedicineType() {
        return medicineType;
    }

}
