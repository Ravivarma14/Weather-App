package com.example.weatherapp.Adapter;

import android.content.Context;
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
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {

        WeatherRecyclerViewModel model=weatherRVList.get(position);
        holder.temperatureTV.setText(model.getTemperature()+"°C");
        Picasso.get().load("https://".concat(model.getIcon())).into(holder.conditionIV);
        holder.windTV.setText(model.getWindspeed() +"Km/h");

        if(model.getIs_day()==1){
            //morning
            Picasso.get().load("https://images.unsplash.com/photo-1544536871-6e891baa163f?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D").into(holder.backgroundIV);
        }
        else{
            Picasso.get().load("https://www.mordeo.org/files/uploads/2020/05/Extreme-Weather-Dark-Clouds-Lightning-4K-Ultra-HD-Mobile-Wallpaper-1152x2048.jpg").into(holder.backgroundIV);
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
