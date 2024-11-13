package com.example.shop.ui.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
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
public class EditProfileActivity extends AppCompatActivity {

    private EditText username, email, currentPassword, newPassword, confirmNewPassword, fullName, phoneNumber, address;
    private Button saveChangesButton, backButton;
    private static final String GET_USER_INFO_URL = "http://10.0.2.2/myapi/get_user_info.php"; // API để lấy thông tin người dùng
    private static final String UPDATE_USER_INFO_URL = "http://10.0.2.2/myapi/update_user_info.php"; // API để cập nhật thông tin người dùng
    private String loggedInUsername; // Username của người dùng đã đăng nhập

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Ánh xạ các thành phần giao diện
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        currentPassword = findViewById(R.id.current_password);
        newPassword = findViewById(R.id.new_password);
        confirmNewPassword = findViewById(R.id.confirm_new_password);
        fullName = findViewById(R.id.full_name);
        phoneNumber = findViewById(R.id.phone_number);
        address = findViewById(R.id.address);
        saveChangesButton = findViewById(R.id.save_changes_button);
        backButton = findViewById(R.id.back_button);

        // Lấy username từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        loggedInUsername = sharedPreferences.getString("username", "");

        // Kiểm tra nếu username không trống
        if (!loggedInUsername.isEmpty()) {
            // Gọi API để lấy thông tin người dùng và điền vào các trường
            loadUserInfo(loggedInUsername);  // Truyền username vào API để lấy thông tin
            username.setText(loggedInUsername); // Điền username vào ô EditText
        } else {
            Toast.makeText(this, "Không thể lấy tên người dùng", Toast.LENGTH_SHORT).show();
        }

        // Thiết lập sự kiện cho nút Lưu thay đổi
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileChanges();
            }
        });

        // Thiết lập sự kiện cho nút Quay lại
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình trước đó
            }
        });
    }

    // Hàm để lấy thông tin người dùng từ API và điền vào form
    private void loadUserInfo(String username) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_USER_INFO_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("success")) {
                                // Điền thông tin vào các trường, ngoại trừ mật khẩu
                                email.setText(jsonObject.getString("email"));
                                fullName.setText(jsonObject.getString("fullname"));
                                phoneNumber.setText(jsonObject.getString("phone"));
                                address.setText(jsonObject.getString("address"));
                            } else {
                                Toast.makeText(EditProfileActivity.this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditProfileActivity.this, "Lỗi xử lý JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditProfileActivity.this, "Lỗi kết nối: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username); // Gửi username tới API
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // Hàm để lưu thay đổi thông tin người dùng
    private void saveProfileChanges() {
        String newUsername = username.getText().toString().trim();
        String newEmail = email.getText().toString().trim();
        String newFullName = fullName.getText().toString().trim();
        String newPhoneNumber = phoneNumber.getText().toString().trim();
        String newAddress = address.getText().toString().trim();
        String currentPass = currentPassword.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmNewPassword.getText().toString().trim();

        // Kiểm tra mật khẩu mới có khớp với xác nhận mật khẩu không
        if (!newPass.isEmpty() && !newPass.equals(confirmPass)) {
            Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_USER_INFO_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(EditProfileActivity.this, "Thông tin đã được cập nhật!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditProfileActivity.this, "Lỗi kết nối: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", newUsername);
                params.put("email", newEmail);
                params.put("full_name", newFullName);
                params.put("phone_number", newPhoneNumber);
                params.put("address", newAddress);

                // Kiểm tra và gửi mật khẩu nếu cần thay đổi
                if (!currentPass.isEmpty() && !newPass.isEmpty()) {
                    params.put("current_password", currentPass);
                    params.put("new_password", newPass);
                }
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        finish();
    }
}
