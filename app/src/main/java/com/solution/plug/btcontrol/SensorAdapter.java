package com.solution.plug.btcontrol;
/**
 * Created by plug on 31/3/18.
 */
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class SensorAdapter extends Adapter<SensorAdapter.MyViewHolder> {
    private List<Sensor> sensorList;

    public class MyViewHolder extends ViewHolder {
        public TextView datatxt;
        public TextView nametxt;

        public MyViewHolder(View view) {
            super(view);
            this.nametxt = (TextView) view.findViewById(R.id.nametxt);
            this.datatxt = (TextView) view.findViewById(R.id.datatxt);
        }
    }

    public SensorAdapter(List<Sensor> sensorList) {
        this.sensorList = sensorList;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor_list_row, parent, false));
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        Sensor sensor = (Sensor) this.sensorList.get(position);
        holder.nametxt.setText(sensor.getName());
        holder.datatxt.setText(sensor.getData());
    }

    public int getItemCount() {
        return this.sensorList.size();
    }
}
