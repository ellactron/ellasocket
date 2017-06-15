package rici.roplug.open.activity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rici.roplug.open.R;
import rici.roplug.open.bll.BLLUtil;
import rici.roplug.open.bll.RemoteServerConnecte;
import rici.roplug.open.view.ResizeLayout;
import rici.roplug.open.view.ShowReaultToast;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

public class RegisteActivity extends BaseActivity {
	private static final int BIGGER = 1;
	private static final int SMALLER = 2;
	private static final int MSG_RESIZE = 1;
	private EditText passwordEditText = null;
	private EditText usernameEditText = null;
	private EditText confirmPasswordEditText = null;
	private Button passwordCleanButton = null;
	private Button usernameCleanButton = null;
	private Button confirmCleanButton = null;
	private Button confirmButton = null;
	private RegisteHandler registedHandler = null;
	private InputHandler mHandler = new InputHandler(); 
	private ScrollView scrollView = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registe_layout);
		registedHandler = new RegisteHandler();
		confirmButton = (Button) findViewById(R.id.btn_registe_confirm);
		Button returnButton = (Button) findViewById(R.id.btn_title_return);
		returnButton.setText(R.string.registe);
		returnButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				RegisteActivity.this.finish();				
			}
		});

		scrollView = (ScrollView)findViewById(R.id.top_layout);
		ResizeLayout layout = (ResizeLayout) findViewById(R.id.root_layout);
		layout.setOnResizeListener(new ResizeLayout.OnResizeListener() {

			public void OnResize(int w, int h, int oldw, int oldh) {
				int change = BIGGER;
				if (h < oldh) {
					change = SMALLER;
				}

				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = change;
				mHandler.sendMessage(msg);
			}
		});
		
		usernameEditText = (EditText) findViewById(R.id.edt_username);
		usernameCleanButton = (Button) findViewById(R.id.btn_username_clean);
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
		passwordEditText = (EditText) findViewById(R.id.edt_password);
		passwordEditText.clearFocus();
		passwordEditText.setCursorVisible(false);
		passwordCleanButton = (Button) findViewById(R.id.btn_password_clean);
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
				if (passwordEditText.getText().toString() != null
						&& !passwordEditText.getText().toString().equals("")) {
					passwordCleanButton.setVisibility(View.VISIBLE);
				} else {
					passwordCleanButton.setVisibility(View.INVISIBLE);
				}
				updateConfirmEnable();
			}
		});
		passwordEditText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				passwordEditText.setCursorVisible(true);

			}
		});
		passwordEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					passwordEditText.setCursorVisible(true);
					if (passwordEditText.getText().toString() != null
							&& !passwordEditText.getText().toString()
									.equals("")) {
						passwordCleanButton.setVisibility(View.VISIBLE);
					} else {
						passwordCleanButton.setVisibility(View.INVISIBLE);
					}
				} else {
					passwordCleanButton.setVisibility(View.INVISIBLE);
				}
			}
		});

		confirmPasswordEditText = (EditText) findViewById(R.id.edt_confirm_password);
		confirmCleanButton = (Button) findViewById(R.id.btn_confirm_password_clean);
		confirmPasswordEditText.addTextChangedListener(new TextWatcher() {

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
				if (confirmPasswordEditText.getText().toString() != null
						&& !confirmPasswordEditText.getText().toString()
								.equals("")) {
					confirmCleanButton.setVisibility(View.VISIBLE);
				} else {
					confirmCleanButton.setVisibility(View.INVISIBLE);
				}
				updateConfirmEnable();
			}
		});
		confirmPasswordEditText
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							if (confirmPasswordEditText.getText().toString() != null
									&& !confirmPasswordEditText.getText()
											.toString().equals("")) {
								confirmCleanButton.setVisibility(View.VISIBLE);
							} else {
								confirmCleanButton
										.setVisibility(View.INVISIBLE);
							}
						} else {
							confirmCleanButton.setVisibility(View.INVISIBLE);
						}
					}
				});
	}

	public void registeClick(View v) {
		switch (v.getId()) {
		case R.id.ll_password:
			passwordEditText.requestFocus();
			break;
		case R.id.ll_username:
			usernameEditText.setCursorVisible(true);
			usernameEditText.requestFocus();
			break;
		case R.id.ll_confirm_password:
			confirmPasswordEditText.requestFocus();
			break;
		case R.id.btn_registe_confirm:
			registeUser();
			break;
		case R.id.btn_password_clean:
			passwordEditText.setText("");
			break;
		case R.id.btn_username_clean:
			usernameEditText.setText("");
			break;
		case R.id.btn_confirm_password_clean:
			confirmPasswordEditText.setText("");
			break;
		}
	}

	public void onResume() {
		super.onResume();
		setCurrentHandler(registedHandler);
	}

	private void registeUser() {
		// (?=.*[~!@#$%^&*()_+'\\-={}\\[\\]:/u0022;'<>?,.\\/])
		String patternString = "^[0-9]*$";

		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = null;
		String username = usernameEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString().trim();
		String confirmPassword = confirmPasswordEditText.getText().toString()
				.trim();
		String format = "^[a-zA-Z0-9]{6,16}$";
		matcher = pattern.matcher(password);

		if (!password.matches(format)) {
			showCheckMessage(String.format(
					getResources().getString(R.string.password_match_error),
					getResources().getString(R.string.password)));
			return;
		}

		if (!username.matches(format)) {
			showCheckMessage(String.format(
					getResources().getString(R.string.password_match_error),
					getResources().getString(R.string.username)));
			return;
		}
		if (!confirmPassword.matches(format)) {
			showCheckMessage(String.format(
					getResources().getString(R.string.password_match_error),
					getResources().getString(R.string.confirm_password)));
			return;
		}

		if (!password.equals(confirmPassword)) {
			showCheckMessage(getResources().getString(
					R.string.password_confirm_error));
			return;
		}
		getSocketBLL().registeNewUser(username, password);
		confirmButton.setText(R.string.registing);
		confirmButton.setEnabled(false);
	}

	private void updateConfirmEnable() {
		boolean isEnable = false;
		if (passwordEditText.getText().toString() != null
				&& !passwordEditText.getText().toString().equals("")) {
			isEnable = true;
		} else if (usernameEditText.getText().toString() != null
				&& !usernameEditText.getText().toString().equals("")) {
			isEnable = true;

		} else if (confirmPasswordEditText.getText().toString() != null
				&& !confirmPasswordEditText.getText().toString().equals("")) {
			isEnable = true;
		}
		confirmButton.setEnabled(isEnable);
	}

	class RegisteHandler extends Handler {
		public RegisteHandler() {

		}

		public RegisteHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BLLUtil.REMOTE_MESSAGE_USER_REGISTE:
				confirmButton.setText(R.string.registe);
				passwordEditText.setText("");
				confirmPasswordEditText.setText("");
				if (msg.arg1 == BLLUtil.RESPONSE_RESULT_SUCCESS) {
					getSocketBLL().setLastLoginUsername(
							usernameEditText.getText().toString().trim());
					usernameEditText.setText("");
					ShowReaultToast.showToast(RegisteActivity.this,
							R.string.registe_success, Toast.LENGTH_SHORT);
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							Intent intent = new Intent(RegisteActivity.this,
									SearchResultActivity.class);
							startActivity(intent);
							RegisteActivity.this.finish();
						}
					}, 3*1000);
				} else if (msg.arg1 == RemoteServerConnecte.MSG_ERROR_ACCOUNT_REGISTER_ALREADY) {
					ShowReaultToast.showToast(RegisteActivity.this, String
							.format(getResources().getString(
									R.string.registe_error_name_exist),
									usernameEditText.getText().toString()
											.trim()), Toast.LENGTH_SHORT);
					usernameEditText.requestFocus();
					usernameEditText.setSelection(usernameEditText.getText()
							.toString().length());// 将光标移至文字末尾
				} else if (msg.arg1 == RemoteServerConnecte.MSG_ERROR_NETWORK_TIMEOUT) {
					usernameEditText.requestFocus();
					usernameEditText.setSelection(usernameEditText.getText()
							.toString().length());// 将光标移至文字末尾
					ShowReaultToast.showToast(RegisteActivity.this,
							R.string.registe_error_timeout, Toast.LENGTH_SHORT);
				}
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
				} else {
					scrollView.fullScroll(ScrollView.FOCUS_DOWN);
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
