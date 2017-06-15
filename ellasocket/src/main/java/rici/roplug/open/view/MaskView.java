package rici.roplug.open.view;

import rici.roplug.open.R;
import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MaskView {
	private Dialog dialog;
	private ImageView loadingView;
	private Animation hyperspaceJumpAnimation = null;

	public MaskView(Activity mActivity) {
		dialog = new Dialog(mActivity, R.style.mask_dialog);
		LinearLayout popView = (LinearLayout) LayoutInflater.from(mActivity)
				.inflate(R.layout.mask_view_layout, null);
		// 关闭按钮
		loadingView = (ImageView) popView.findViewById(R.id.img_loading);
		// loadingView.setImageResource(R.drawable.loading);
		hyperspaceJumpAnimation = AnimationUtils.loadAnimation(mActivity,
				R.anim.loading_animation);
		// 使用ImageView显示动画
		loadingView.startAnimation(hyperspaceJumpAnimation);
		dialog.setContentView(popView, new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
	}

	public void show() {
		dialog.show();
	}

	public void hide() {
		hyperspaceJumpAnimation.cancel();
		loadingView.clearAnimation();
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}
}
