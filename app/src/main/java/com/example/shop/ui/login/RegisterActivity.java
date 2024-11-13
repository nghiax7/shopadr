package com.example.shop.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shop.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, email, password, confirmPassword, fullName, phoneNumber, address;
    private Button registerButton,backButton;
    private static final String REGISTER_URL = "http://10.0.2.2/myapi/register.php"; // URL đến file register.php trên máy chủ

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ các thành phần giao diện
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        fullName = findViewById(R.id.full_name);
        phoneNumber = findViewById(R.id.phone_number);
        address = findViewById(R.id.address);
        registerButton = findViewById(R.id.register_button);
        backButton = findViewById(R.id.back_button);

        // Thiết lập sự kiện cho nút "Đăng ký"
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ các trường nhập liệu
                String usernameText = username.getText().toString().trim();
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();
                String confirmPasswordText = confirmPassword.getText().toString().trim();
                String fullNameText = fullName.getText().toString().trim();
                String phoneNumberText = phoneNumber.getText().toString().trim();
                String addressText = address.getText().toString().trim();

                // Kiểm tra tính hợp lệ của dữ liệu
                if (usernameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() ||
                        confirmPasswordText.isEmpty() || fullNameText.isEmpty() ||
                        phoneNumberText.isEmpty() || addressText.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else if (!passwordText.equals(confirmPasswordText)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                } else {
                    // Thực hiện đăng ký
                    registerUser(usernameText, emailText, passwordText, fullNameText, phoneNumberText, addressText);
                }
            }
        });


        // Thiết lập sự kiện cho nút "Quay lại"
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng RegisterActivity và quay lại LoginActivity
                finish();
            }
        });
    }

    private void registerUser(String username, String email, String password, String fullName, String phone, String address) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if (status.equals("success")) {
                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                finish(); // Đóng Activity sau khi đăng ký thành công
                            } else {
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Đã xảy ra lỗi khi xử lý phản hồi từ máy chủ", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("email", email);
                params.put("fullname", fullName);
                params.put("phone", phone);
                params.put("address", address);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
