package redgao.leoxun.sexynbeauty;

import java.util.ArrayList;

import redgao.leoxun.sexynbeauty.model.GalleryItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryListAdapter extends ArrayAdapter<String> {

    private final GalleryFragment fragment;
    private ArrayList<GalleryItem> listItems;

    public GalleryListAdapter(GalleryFragment fragment, ArrayList<GalleryItem> listItems, String[] name) {
        super(fragment.getActivity(), R.layout.gallery_item, name);

        this.fragment = fragment;
        this.listItems = listItems;
    }

    public static class ViewHolder {
        public TextView name;
        public ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.gallery_item, null);

            holder = new ViewHolder();
            holder.name = (TextView) vi.findViewById(R.id._galleryItemTitle);
            holder.image = (ImageView) vi.findViewById(R.id._galleryItemImage);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.name.setText(listItems.get(position).getImageUrl());
//        fragment.getImageLoader().displayImage(listItems.get(position).getImageUrl(), holder.image);

        vi.setOnClickListener(new OnItemClickListener(position));

        return vi;
    }

    private class OnItemClickListener implements OnClickListener {
        private int mPosition;

        OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            
        }
    }

}
