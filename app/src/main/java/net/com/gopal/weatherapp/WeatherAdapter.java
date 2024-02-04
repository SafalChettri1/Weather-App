package net.com.gopal.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

private Context context;
private ArrayList<WeatherModel> weatherModelArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherModel> weatherModelArrayList) {
        this.context = context;
        this.weatherModelArrayList = weatherModelArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycleview_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {

        WeatherModel weatherModel =weatherModelArrayList.get(position);
        holder.temperature.setText(weatherModel.getTemperature()+"°c");

        Picasso.get().load("http:".concat(weatherModel.getIcon())).into(holder.condition);
        holder.windSpeed.setText(weatherModel.getWindSpeed()+"Km/h");

        // date and time format
        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-mm-dd hh:mm");
        SimpleDateFormat output= new SimpleDateFormat("hh:mm aa");
        try{
            Date date = simpleDateFormat.parse(weatherModel.getTime());
            holder.time.setText(output.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return weatherModelArrayList != null ? weatherModelArrayList.size() : 0;    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView temperature, time, windSpeed;
        private ImageView condition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            temperature = itemView.findViewById(R.id.temperature);
            time = itemView.findViewById(R.id.time);
            windSpeed = itemView.findViewById(R.id.windSpeed);
            condition = itemView.findViewById(R.id.condition);

        }
    }
}
