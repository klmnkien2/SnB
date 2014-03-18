package dovietkien.me.sexynbeauty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MenuAdapter extends ArrayAdapter<String> {

    private final Context context;
    private String[] names;
    
    static class ViewHolder { 
    	TextView text;
    	ImageView image;
    }

    public MenuAdapter(Context context, String[] names) {
        super(context, R.layout.drawer_list_item, names);
        this.context = context;
        this.names = names;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	View rowView = convertView;
    	ViewHolder viewHolder;
    	if (rowView == null) {
    	      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	      rowView = inflater.inflate(R.layout.drawer_list_item, null);
    	      viewHolder = new ViewHolder();
    	      viewHolder.text = (TextView) rowView.findViewById(R.id.textView);
    	      viewHolder.image = (ImageView) rowView.findViewById(R.id.imageView);
    	      rowView.setTag(viewHolder);
    		} 
    		else { 
    			viewHolder = (ViewHolder) rowView.getTag(); 
        } 
              
        viewHolder.text.setText(names[position]);  
        
        if(position == ((ListView)parent).getCheckedItemPosition())
            viewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.gtfo));
        else 
            viewHolder.image.setImageDrawable(context.getResources().getDrawable(R.color.full_transparent));
        
        return rowView;       
    }

}