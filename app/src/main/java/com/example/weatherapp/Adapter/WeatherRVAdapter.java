package com.example.weatherapp.Adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.Model.WeatherRecyclerViewModel;
import com.example.weatherapp.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {

    Context context;
    ArrayList<WeatherRecyclerViewModel> weatherRVList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRecyclerViewModel> weatherRVModelList) {
        this.context = context;
        weatherRVList = weatherRVModelList;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WeatherRecyclerViewModel model=weatherRVList.get(position);
        holder.temperatureTV.setText(model.getTemperature()+"°C");
        Picasso.get().load("https://".concat(model.getIcon())).into(holder.conditionIV);
        holder.windTV.setText(model.getWindspeed() +"Km/h");

        if(model.getIs_day()==1){
            //morning
            Picasso.get().load("https://images.unsplash.com/photo-1544536871-6e891baa163f?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D").into(holder.backgroundIV);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setupColorsforViews(holder,context.getColor(R.color.black));
            }
        }
        else{
            Picasso.get().load("https://wallpapers.com/images/hd/cell-phone-2160-x-3840-background-fvw01yvkuyjjgm74.jpg").into(holder.backgroundIV);
            //holder.backgroundIV.setImageDrawable(context.getDrawable(R.drawable.img));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setupColorsforViews(holder,context.getColor(R.color.white));
            }
        }

        SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output=new SimpleDateFormat("hh:mm aa");
        try {
            Date t=input.parse(model.getTime());
            holder.timeTV.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherRVList.size();
    }

    private void setupColorsforViews(WeatherRVAdapter.ViewHolder holder, int color){
        holder.timeTV.setTextColor(color);
        holder.windTV.setTextColor(color);
        holder.temperatureTV.setTextColor(color);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView windTV,temperatureTV,timeTV;
        ImageView conditionIV,backgroundIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTV=itemView.findViewById(R.id.tvWindSpeed);
            temperatureTV=itemView.findViewById(R.id.tvTemperature);
            timeTV=itemView.findViewById(R.id.tvTime);
            conditionIV=itemView.findViewById(R.id.ivCondition);
            backgroundIV=itemView.findViewById(R.id.ivCardBack);
        }
    }

}
