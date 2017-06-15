package rici.roplug.open.adapter;

import java.util.ArrayList;

import rici.roplug.open.view.SocketElectricView;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;


public class SocketElectricPagerAdapter extends PagerAdapter {
	private ArrayList<SocketElectricView> list = null;
	private OnReloadListener mListener;

	public SocketElectricPagerAdapter(ArrayList<SocketElectricView> list) {
		this.list = list;

	}

	public void reLoad(ArrayList<SocketElectricView> list) {
		this.list = list;
		this.notifyDataSetChanged();
	}

	public void setOnReloadListener(OnReloadListener listener) {
		this.mListener = listener;
	}

	public interface OnReloadListener {
		public void onReload();
	}

	@Override
	public void destroyItem(ViewGroup view, int position, Object object) {
		if (position < list.size())
			view.removeView(list.get(position));
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		((ViewPager) container).addView(list.get(position));
		// 给每个item的view 就是刚才views存放着的view
		return list.get(position);
	}

}
