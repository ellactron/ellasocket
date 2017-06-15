package rici.roplug.open.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rici.wifi.IRiCiWifiServer;

import rici.roplug.open.R;
import rici.roplug.open.bll.BLLUtil;
import rici.roplug.open.bll.SocketBLL;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class ModifyPasswordActivity extends BaseActivity {
	private EditText oldPasswordEditText = null;
	private EditText newPasswordEditText = null;
	private EditText confirmPasswordEditText = null;
	private Button oldCleanButton = null;
	private Button newCleanButton = null;
	private Button confirmCleanButton = null;
	private Button confirmButton = null;
	private CheckBox showCheckBox = null;
	private ModifyPasswordHandler modifyPasswordHandler = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.modify_password_layout);
		modifyPasswordHandler = new ModifyPasswordHandler();
		confirmButton = (Button) findViewById(R.id.btn_modify_password_confirm);
		Button returnButton = (Button) findViewById(R.id.btn_title_return);
		returnButton.setText(R.string.modify_password);		
		returnButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ModifyPasswordActivity.this.finish();
			}
		});
		oldPasswordEditText = (EditText) findViewById(R.id.edt_old_password);
		oldPasswordEditText.clearFocus();
		oldPasswordEditText.setCursorVisible(false);
		oldCleanButton = (Button) findViewById(R.id.btn_old_password_clean);
		oldPasswordEditText.addTextChangedListener(new TextWatcher() {

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
				if (oldPasswordEditText.getText().toString() != null
						&& !oldPasswordEditText.getText().toString().equals("")) {
					oldCleanButton.setVisibility(View.VISIBLE);
				} else {
					oldCleanButton.setVisibility(View.INVISIBLE);
				}
				updateConfirmEnable();
			}
		});
		oldPasswordEditText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				oldPasswordEditText.setCursorVisible(true);

			}
		});
		oldPasswordEditText
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							oldPasswordEditText.setCursorVisible(true);
							if (oldPasswordEditText.getText().toString() != null
									&& !oldPasswordEditText.getText()
											.toString().equals("")) {
								oldCleanButton.setVisibility(View.VISIBLE);
							} else {
								oldCleanButton.setVisibility(View.INVISIBLE);
							}
						} else {
							oldCleanButton.setVisibility(View.INVISIBLE);
						}
					}
				});
		newPasswordEditText = (EditText) findViewById(R.id.edt_new_password);
		newCleanButton = (Button) findViewById(R.id.btn_new_password_clean);
		newPasswordEditText.addTextChangedListener(new TextWatcher() {

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
				if (newPasswordEditText.getText().toString() != null
						&& !newPasswordEditText.getText().toString().equals("")) {
					newCleanButton.setVisibility(View.VISIBLE);
				} else {
					newCleanButton.setVisibility(View.INVISIBLE);
				}
				updateConfirmEnable();
			}
		});

		newPasswordEditText
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							if (newPasswordEditText.getText().toString() != null
									&& !newPasswordEditText.getText()
											.toString().equals("")) {
								newCleanButton.setVisibility(View.VISIBLE);
							} else {
								newCleanButton.setVisibility(View.INVISIBLE);
							}
						} else {
							newCleanButton.setVisibility(View.INVISIBLE);
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
		showCheckBox = (CheckBox) findViewById(R.id.cb_show_password);
		showCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					oldPasswordEditText
							.setInputType(InputType.TYPE_CLASS_TEXT
									|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					newPasswordEditText
							.setInputType(InputType.TYPE_CLASS_TEXT
									|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					confirmPasswordEditText
							.setInputType(InputType.TYPE_CLASS_TEXT
									|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					oldPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					newPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					confirmPasswordEditText
							.setInputType(InputType.TYPE_CLASS_TEXT
									| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}

				CharSequence text = oldPasswordEditText.getText();
				// Debug.asserts(text instanceof Spannable);
				if (text instanceof Spannable) {
					Spannable spanText = (Spannable) text;
					Selection.setSelection(spanText, text.length());
				}
				text = newPasswordEditText.getText();
				// Debug.asserts(text instanceof Spannable);
				if (text instanceof Spannable) {
					Spannable spanText = (Spannable) text;
					Selection.setSelection(spanText, text.length());
				}
				text = confirmPasswordEditText.getText();
				// Debug.asserts(text instanceof Spannable);
				if (text instanceof Spannable) {
					Spannable spanText = (Spannable) text;
					Selection.setSelection(spanText, text.length());
				}

			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		setCurrentHandler(modifyPasswordHandler);
	}

	public void modifyPasswordClick(View v) {
		switch (v.getId()) {
		case R.id.ll_old_password:
			oldPasswordEditText.setCursorVisible(true);
			oldPasswordEditText.requestFocus();
			break;
		case R.id.ll_new_password:
			newPasswordEditText.requestFocus();
			break;
		case R.id.ll_confirm_password:
			confirmPasswordEditText.requestFocus();
			break;
		case R.id.btn_modify_password_confirm:
			confirmPassword();
			break;
		case R.id.btn_old_password_clean:
			oldPasswordEditText.setText("");
			break;
		case R.id.btn_new_password_clean:
			newPasswordEditText.setText("");
			break;
		case R.id.btn_confirm_password_clean:
			confirmPasswordEditText.setText("");
			break;
		}
	}
	String newPassword;
	private void confirmPassword() {
		// (?=.*[~!@#$%^&*()_+'\\-={}\\[\\]:/u0022;'<>?,.\\/])
		String patternString = "^[0-9]*$";

		Pattern pattern = Pattern.compile(patternString);
		String oldPassword = oldPasswordEditText.getText().toString().trim();
		newPassword = newPasswordEditText.getText().toString().trim();
		String confirmPassword = confirmPasswordEditText.getText().toString()
				.trim();
		String format = "^[a-zA-Z0-9]{5,17}$";        
		Matcher matcher = pattern.matcher(oldPassword);

		if (!oldPassword.matches(format)) {
			showCheckMessage(String.format(
					getResources().getString(R.string.password_match_error),
					getResources().getString(R.string.old_password)));
			return;
		}

		if (!newPassword.matches(format)) {
			showCheckMessage(String.format(
					getResources().getString(R.string.password_match_error),
					getResources().getString(R.string.new_password)));
			return;
		}
		if (!confirmPassword.matches(format)) {
			showCheckMessage(String.format(
					getResources().getString(R.string.password_match_error),
					getResources().getString(R.string.confirm_password)));
			return;
		}

		if (newPassword.equals(oldPassword)) {
			showCheckMessage(getResources().getString(R.string.password_same));
			return;
		}

		if (!newPassword.equals(confirmPassword)) {
			showCheckMessage(getResources().getString(
					R.string.password_confirm_error));
			return;
		}
		
		getSocketBLL().modifyPassword(newPassword);
		
		oldPasswordEditText.setText("");
		newPasswordEditText.setText("");
		confirmPasswordEditText.setText("");
		showCheckBox.setChecked(false);
		confirmButton.setEnabled(false);
	}
	
	private void updateConfirmEnable() {
		boolean isEnable = false;
		if (oldPasswordEditText.getText().toString() != null
				&& !oldPasswordEditText.getText().toString().equals("")) {
			isEnable = true;
		} else if (newPasswordEditText.getText().toString() != null
				&& !newPasswordEditText.getText().toString().equals("")) {
			isEnable = true;

		} else if (confirmPasswordEditText.getText().toString() != null
				&& !confirmPasswordEditText.getText().toString().equals("")) {
			isEnable = true;
		}
		confirmButton.setEnabled(isEnable);
	}

	class ModifyPasswordHandler extends Handler {
		public ModifyPasswordHandler() {

		}

		public ModifyPasswordHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BLLUtil.REMOTE_MESSAGE_CHANGE_LOGIN_PASSWORD:
				if (msg.arg1 == IRiCiWifiServer.RESPONSE_RESULT_SUCCESS) {
					showCheckMessage(getResources().getString(
							R.string.change_password_success));
					updateData();
				}else{
					showCheckMessage(getResources().getString(
							R.string.change_password_failure));
				}
				break;
			
			}
		}
	}

	public void updateData() {
		SocketBLL.getInstance().setCurrentPassword(newPassword);
		if(getSocketBLL().getAutoLogin() || getSocketBLL().isRemeberPassword()){
			getSocketBLL().setLastLoginPassword(newPassword);
		}
	}
}
