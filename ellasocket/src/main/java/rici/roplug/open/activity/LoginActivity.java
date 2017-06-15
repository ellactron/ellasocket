package rici.roplug.open.activity;

import com.rici.wifi.util.L;

import rici.roplug.open.R;
import rici.roplug.open.bll.BLLUtil;
import rici.roplug.open.view.ResizeLayout;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {
	private static final int BIGGER = 1;
	private static final int SMALLER = 2;
	private static final int MSG_RESIZE = 1;
	private boolean isPasswordClick = false;
	private EditText passwordEditText = null;
	private EditText usernameEditText = null;
	// private EditText hideEditText = null;
	private Button passwordShowHide = null;
	private Button usernameCleanButton = null;
	private Button confirmButton = null;
	private long exitTime = 0;
	private LoginHandler loginHandler = null;
	private InputHandler mHandler = new InputHandler();
	private ScrollView scrollView = null;
	private CheckBox rememberPassword = null;
	private CheckBox autoLogin = null;
	private boolean isShowPassword = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		loginHandler = new LoginHandler();
		confirmButton = (Button) findViewById(R.id.btn_login_confirm);
		rememberPassword = (CheckBox) findViewById(R.id.cb_login_remember_password);
		autoLogin = (CheckBox) findViewById(R.id.cb_login_auto);
		// hideEditText = (EditText)findViewById(R.id.edt_hide);
		usernameCleanButton = (Button) findViewById(R.id.btn_username_clean);
		passwordShowHide = (Button) findViewById(R.id.btn_password_show_hide);
		passwordEditText = (EditText) findViewById(R.id.edt_password);
		usernameEditText = (EditText) findViewById(R.id.edt_username);
		passwordEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				updateConfirmEnable();
			}
		});
		passwordEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					isPasswordClick = true;
				}
			}
		});

		passwordEditText.setText(getSocketBLL().getLastLoginPassword());
		usernameEditText.setText(getSocketBLL().getLastLoginUsername());
		if (usernameEditText.getText().toString().trim().length() == 0) {
			usernameEditText.requestFocus();
		} else {
			usernameCleanButton.setVisibility(View.INVISIBLE);
			usernameEditText.clearFocus();
			if (passwordEditText.getText().toString().trim().length() == 0) {
				passwordEditText.requestFocus();
			} else {
				passwordEditText.requestFocus();
				passwordEditText.setSelection(passwordEditText.getText().toString()
						.length());  
				rememberPassword.setChecked(true);
			}
		}
		usernameEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (usernameEditText.getText().toString() != null
						&& !usernameEditText.getText().toString().equals("")) {
					usernameCleanButton.setVisibility(View.VISIBLE);
				} else {
					usernameCleanButton.setVisibility(View.INVISIBLE);
				}
				updateConfirmEnable();
			}
		});

		usernameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					isPasswordClick = false;
					if (usernameEditText.getText().toString() != null
							&& !usernameEditText.getText().toString()
									.equals("")) {
						usernameCleanButton.setVisibility(View.VISIBLE);
					} else {
						usernameCleanButton.setVisibility(View.INVISIBLE);
					}
				} else {
					usernameCleanButton.setVisibility(View.INVISIBLE);
				}
			}
		});
		rememberPassword
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (!isChecked) {
							if (autoLogin != null) {
								autoLogin.setChecked(false);
							}
						}
					}
				});

		updateConfirmEnable();
		autoLogin.setChecked(getSocketBLL().getAutoLogin());
		autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					if (rememberPassword != null) {
						rememberPassword.setChecked(true);
					}
				}
			}
		});
		scrollView = (ScrollView) findViewById(R.id.top_layout);
		ResizeLayout layout = (ResizeLayout) findViewById(R.id.root_layout);
		layout.setOnResizeListener(new ResizeLayout.OnResizeListener() {

			public void OnResize(int w, int h, int oldw, int oldh) {
				int change = BIGGER;
				if (h < oldh) {
					change = SMALLER;

				}
				if (isPasswordClick) {
					passwordEditText.requestFocus();
				}
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = change;
				msg.arg2 = oldh;
				mHandler.sendMessage(msg);
			}
		});

	}

	public void loginClick(View v) {
		switch (v.getId()) {
		case R.id.ll_username:
			usernameEditText.setCursorVisible(true);
			usernameEditText.requestFocus();
			isPasswordClick = false;
			break;
		case R.id.ll_password:
			passwordEditText.setCursorVisible(true);
			passwordEditText.requestFocus();
			isPasswordClick = true;
			break;
		case R.id.btn_username_clean:
			usernameEditText.setText("");
			break;
		case R.id.btn_password_show_hide:
			isShowPassword = !isShowPassword;
			passwordEditText
					.setInputType(isShowPassword ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
							: InputType.TYPE_CLASS_TEXT
									| InputType.TYPE_TEXT_VARIATION_PASSWORD);

			passwordEditText.setSelection(passwordEditText.getText().toString()
					.length());
			passwordShowHide
					.setBackgroundResource(isShowPassword ? R.drawable.button_password_show
							: R.drawable.button_password_hide);
			break;
		case R.id.btn_login_confirm:
			/*
			 * ElectricityAnalyseAloneView electricityAnalyseView = new
			 * ElectricityAnalyseAloneView(LoginActivity.this);
			 * 
			 * Window electricityWindow = electricityAnalyseView.getWindow();
			 * electricityWindow
			 * .setWindowAnimations(R.style.dialog_show_animation); // ÉèÖÃ´°¿Úµ¯³ö¶¯»­
			 * electricityAnalyseView.setCanceledOnTouchOutside(false);
			 * electricityAnalyseView.show();
			 */
			confirmPassword();
			break;
		case R.id.btn_registe:
			Intent intent = new Intent(LoginActivity.this,
					RegisteActivity.class);
			startActivity(intent);
			break;
		}
	}

	private void confirmPassword() {
		String password = passwordEditText.getText().toString().trim();
		String username = usernameEditText.getText().toString().trim();
		String format = "^[a-zA-Z0-9]{6,16}$";
		if ((password != null && password.toString().trim().length() > 0)
				&& (username != null && username.toString().trim().length() > 0)) {
			if (!password.matches(format)) {
				showCheckMessage(String
						.format(getResources().getString(
								R.string.password_match_error), getResources()
								.getString(R.string.password)));
				return;
			}
			if (!username.matches(format)) {
				showCheckMessage(String
						.format(getResources().getString(
								R.string.password_match_error), getResources()
								.getString(R.string.username)));
				return;
			}

			sendWaitMessage(loginHandler, BLLUtil.WIFI_CONNECTED_TIMEOUT);
			getSocketBLL().loginUser(username, password);
			// passwordEditText.setText("");
			passwordEditText.setEnabled(false);
			usernameEditText.setEnabled(false);
			confirmButton.setText(R.string.logining);
			confirmButton.setEnabled(false);
		}
	}

	public void onResume() {
		super.onResume();
		setCurrentHandler(loginHandler);
		if (getSocketBLL().getAutoLogin() && !getSocketBLL().isLoginOut()) {
			Handler mh = new Handler();
			mh.postDelayed(new Runnable() {
				@Override
				public void run() {
					confirmPassword();
				}
			}, 2000);
		}
	}

	private void updateConfirmEnable() {
		boolean isEnable = false;
		L.d(passwordEditText.getText().toString()+","+usernameEditText.getText().toString());
		if (passwordEditText.getText().toString() != null
				&& !passwordEditText.getText().toString().equals("")) {
			if (usernameEditText.getText().toString() != null
					&& !usernameEditText.getText().toString().equals("")) {
				isEnable = true;
			} else {
				isEnable = false;
			}
		}
		confirmButton.setEnabled(isEnable);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.exit_click_again),
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	class LoginHandler extends Handler {
		public LoginHandler() {

		}

		public LoginHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BLLUtil.REMOTE_MESSAGE_USER_LOGIN:
				stopWaitMessage();
				confirmButton.setText(R.string.login);
				passwordEditText.setEnabled(true);
				usernameEditText.setEnabled(true);
				confirmButton.setEnabled(true);
				updateConfirmEnable();
				if (msg.arg1 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
					if (rememberPassword != null) {
						getSocketBLL().setRemeberPassword(rememberPassword.isChecked());
						getSocketBLL().setLastLoginPassword(
								rememberPassword.isChecked() ? passwordEditText
										.getText().toString().trim() : "");
					}
					if (autoLogin != null) {
						getSocketBLL().setAutoLogin(autoLogin.isChecked());
					}
					getSocketBLL().setLastLoginUsername(
							usernameEditText.getText().toString().trim());
					Intent searchInent = new Intent(LoginActivity.this,
							SearchResultActivity.class);
					startActivity(searchInent);
					LoginActivity.this.finish();
				} else if (msg.arg1 == BLLUtil.MSG_ERROR_NETWORK_TIMEOUT) {
					showCheckMessage(R.string.net_timeout);
				} else {
					showCheckMessage(R.string.login_error);
				}
				break;

			case BLLUtil.REMOTE_NETWORK_ERROR_TIMEOUT:
				stopWaitMessage();
				confirmButton.setText(R.string.login);
				confirmButton.setEnabled(true);
				passwordEditText.setEnabled(true);
				usernameEditText.setEnabled(true);
				showCheckMessage(R.string.net_timeout);
				break;
			}
		}
	}

	class InputHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESIZE: {
				if (msg.arg1 == BIGGER) {
					findViewById(R.id.bottom_layout)
							.setVisibility(View.VISIBLE);
				} else {
					findViewById(R.id.bottom_layout).setVisibility(View.GONE);
					scrollView.scrollTo(0, msg.arg2);
					if (isPasswordClick) {
						passwordEditText.requestFocus();
					} else {
						usernameEditText.requestFocus();
					}
				}
			}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

}
