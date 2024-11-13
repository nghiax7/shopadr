package com.example.shop.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shop.Product;
import com.example.shop.R;
import com.example.shop.Slide;
import com.example.shop.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private ViewPager2 viewPager;
    private ProductAdapter productAdapter;
    private SlideAdapter slideAdapter;
    private List<Product> productList;
    private List<Slide> slideList;
    private List<Product> currentPageProductList;

    private static final int PAGE_SIZE = 4; // Số sản phẩm trên mỗi trang
    private int currentPage = 0; // Trang hiện tại

    private static final String URL_PRODUCTS = "http://10.0.2.2/myapi/get_products.php";
    private static final String URL_SLIDES = "http://10.0.2.2/myapi/get_slides.php";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup ViewPager cho slideshow
        viewPager = binding.viewPager;
        slideList = new ArrayList<>();
        slideAdapter = new SlideAdapter(slideList);
        viewPager.setAdapter(slideAdapter);

        // Setup RecyclerView cho danh sách sản phẩm
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productList = new ArrayList<>();
        currentPageProductList = new ArrayList<>();
        productAdapter = new ProductAdapter(currentPageProductList);
        recyclerView.setAdapter(productAdapter);

        // TextView để hiển thị số trang
        TextView pageNumberTextView = binding.pageNumber;

        // Gọi API để tải toàn bộ sản phẩm và slides
       fetchSlides();
        fetchProducts();

        // Thiết lập sự kiện cho các nút "Next" và "Previous"
        binding.buttonNext.setOnClickListener(v -> nextPage());
        binding.buttonPrevious.setOnClickListener(v -> previousPage());

        return root;
    }

    private void fetchSlides() {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_SLIDES, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                JSONArray slides = response.getJSONArray("slides");
                                for (int i = 0; i < slides.length(); i++) {
                                    JSONObject slide = slides.getJSONObject(i);
                                    String imageUrl = slide.optString("image_url", null);
                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        slideList.add(new Slide(
                                                slide.getInt("id"),
                                                imageUrl
                                        ));
                                    } else {
                                        // Log cảnh báo nếu image_url không tồn tại hoặc trống
                                        Log.w("fetchSlides", "Slide with missing image_url at index " + i);
                                    }
                                }
                                slideAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getContext(), "Không có slide nào", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("fetchSlides", "JSON parsing error", e);
                            Toast.makeText(getContext(), "Đã xảy ra lỗi khi phân tích dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("fetchSlides", "Volley error", error);
                        Toast.makeText(getContext(), "Lỗi khi tải slides", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }


    private void fetchProducts() {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_PRODUCTS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                JSONArray products = response.getJSONArray("products");
                                for (int i = 0; i < products.length(); i++) {
                                    JSONObject product = products.getJSONObject(i);
                                    productList.add(new Product(
                                            product.getInt("id"),
                                            product.getString("name"),
                                            product.getDouble("price"),
                                            product.getDouble("sale_price"),
                                            product.getString("thumbnail")
                                    ));
                                }
                                loadPage(0); // Hiển thị trang đầu tiên
                            } else {
                                Toast.makeText(getContext(), "Không có sản phẩm nào", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Lỗi khi tải sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void loadPage(int pageNumber) {
        currentPageProductList.clear();

        int start = pageNumber * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, productList.size());

        for (int i = start; i < end; i++) {
            currentPageProductList.add(productList.get(i));
        }

        productAdapter.notifyDataSetChanged();

        // Cập nhật trạng thái nút
        binding.buttonPrevious.setEnabled(pageNumber > 0);
        binding.buttonNext.setEnabled(end < productList.size());

        // Cập nhật số trang
        binding.pageNumber.setText("Page " + (pageNumber + 1));

        currentPage = pageNumber;
    }

    private void nextPage() {
        if ((currentPage + 1) * PAGE_SIZE < productList.size()) {
            loadPage(currentPage + 1);
        }
    }

    private void previousPage() {
        if (currentPage > 0) {
            loadPage(currentPage - 1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
