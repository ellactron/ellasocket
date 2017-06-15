package rici.roplug.open.adapter;

import java.util.List;

import rici.roplug.open.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

public class PopupwindowRadioButtonListAdapter extends BaseAdapter {
    private List<String> mList = null;
    private Context mContext = null;
    private int selectedIndex = -1;

    public PopupwindowRadioButtonListAdapter(Context context, List<String> list, int defaultIndex) {
        mContext = context;
        mList = list;
        selectedIndex = defaultIndex;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        AppItem appItem;
        if (convertView == null) {
            View v = LayoutInflater.from(mContext).inflate(
                    R.layout.popupwindow_list_radio_item_layout, null);
            appItem = new AppItem();
            appItem.radioButton = (RadioButton) v.findViewById(R.id.rb_popupwindow);
            appItem.radioButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedIndex = position;
                    PopupwindowRadioButtonListAdapter.this.notifyDataSetChanged();
                }
            });

            appItem.radioButton.setText(mList.get(position));
            v.setTag(appItem);
            convertView = v;
        } else {
            appItem = (AppItem) convertView.getTag();
        }

        if (position == selectedIndex) {
            appItem.radioButton.setChecked(true);
        } else {
            appItem.radioButton.setChecked(false);
        }
        return convertView;
    }

    public int getSelectedItem() {
        return selectedIndex;
    }

    class AppItem {
        RadioButton radioButton;
    }
}
