package com.example.skillswap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.List;

public class SwipeAdapter extends BaseAdapter {

    private Context context;
    private List<Thing> list;

    public SwipeAdapter(Context context, List<Thing> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_koloda, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.image);
//            holder.nameTextView = convertView.findViewById(R.id.name_thing);
//            holder.descriptionTextView = convertView.findViewById(R.id.description_thing);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Thing thing = list.get(position);

        for (Thing thinj : list) {
            if (thinj.getImageThing() == null) {
                Log.e("LoadImage", "URL de imagen es null para el objeto: " + thinj.getNameThing());
            }
        }


//        holder.nameTextView.setText(thing.getNameThing());
//        holder.descriptionTextView.setText(thing.getDescriptionThing());
        Glide.with(context)
                .load(thing.getImageThing())
                .apply(new RequestOptions().placeholder(R.drawable.close).error(R.drawable.close))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Aquí puedes obtener información del error
                        Log.e("Glide", "Error cargando imagen", e);
                        return false; // return false para que Glide maneje el error y muestre la imagen de error
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // La imagen se cargó correctamente
                        return false;
                    }
                })
                .into(holder.imageView);



        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView descriptionTextView;
    }
}
