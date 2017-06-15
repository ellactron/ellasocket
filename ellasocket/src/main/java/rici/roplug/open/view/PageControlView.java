package rici.roplug.open.view;

import rici.roplug.open.R;
import rici.roplug.open.view.ScrollLayout.OnScreenChangeListener;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageControlView extends LinearLayout {
	private Context context;

	private int count;

	public void bindScrollViewGroup(ScrollLayout scrollViewGroup) {
		this.count = scrollViewGroup.getChildCount();
		generatePageControl(scrollViewGroup.getCurrentScreenIndex());

		scrollViewGroup.setOnScreenChangeListener(new OnScreenChangeListener() {

			@Override
			public void onScreenChange(int currentIndex) {
				// TODO Auto-generated method stub
				generatePageControl(currentIndex);
			}
		});
	}

	public PageControlView(Context context) {
		super(context);
		this.init(context);
	}

	public PageControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context);
	}

	private void init(Context context) {
		this.context = context;
	}

	private void generatePageControl(int currentIndex) {
		this.removeAllViews();
		int pageNo = currentIndex + 1; // 第几页
		int pageSum = this.count; // 总共多少页
		for (int i = 0; i < pageSum; i++) {
			ImageView imageView = new ImageView(context);
			if (i + 1 == pageNo) {
				imageView.setImageResource(R.drawable.dot_select);
			} else {
				imageView.setImageResource(R.drawable.dot_normal);
			}
			this.addView(imageView);
		}
	}
}
