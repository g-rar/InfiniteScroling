package com.example.infinitescroling.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.infinitescroling.R;
import com.example.infinitescroling.models.AcademicInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EditAcademicAdapter extends ArrayAdapter {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private AcademicEditable academicEditable;
    private boolean edit;

    public EditAcademicAdapter(Context context, AcademicEditable academicEditable, ArrayList<AcademicInfo> academics, boolean edit){
        super(context, 0, academics);
        this.academicEditable = academicEditable;
        this.edit = edit;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AcademicInfo academicInfo = (AcademicInfo) getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.edit_academics_row, parent, false);
        }

        TextView tvTitle = convertView.findViewById(R.id.textView_titleLbl);
        TextView tvInst = convertView.findViewById(R.id.textView_intitutionName);
        TextView tvBeginDate = convertView.findViewById(R.id.textView_startDate);
        TextView tvEndDate = convertView.findViewById(R.id.textView_endDate);
        ImageButton btnEdit = convertView.findViewById(R.id.imageButton_editAcadmic);
        ImageButton btnDel = convertView.findViewById(R.id.imageButton_deleteAcademic);

        tvTitle.setText(academicInfo.getTitle());
        tvInst.setText(academicInfo.getInstitution());
        tvBeginDate.setText(simpleDateFormat.format(academicInfo.getBeginDate()));
        tvEndDate.setText(simpleDateFormat.format(academicInfo.getEndDate()));
        if(edit) {
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    academicEditable.editAcademicOnClick(position);
                }
            });
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    academicEditable.deleteAcademicOnClick(position);
                }
            });
        }
        else{
            btnEdit.setVisibility(View.GONE);
            btnDel.setVisibility(View.GONE);
        }


        return convertView;
    }

    public interface AcademicEditable{
        void editAcademicOnClick(int position);
        void deleteAcademicOnClick(int position);
    }
}