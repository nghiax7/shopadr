package com.example.shop.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shop.R;
import com.example.shop.Product;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private static final String BASE_URL = "http://10.0.2.2/shop/";

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        // Định dạng giá và giá khuyến mãi sang tiền VND
        String priceVND = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(product.getPrice())
                .replace("₫", "đ"); // Thay ký hiệu mặc định của VND thành "đ"

        String salePriceVND = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(product.getSalePrice())
                .replace("₫", "đ");
        // Thiết lập dữ liệu cho các TextView
        holder.name.setText(product.getName());
        holder.price.setText("Giá: " + priceVND);
        holder.salePrice.setText("Giá khuyến mãi: " + salePriceVND);


        // Kiểm tra xem thumbnail là URL trực tiếp hay là đường dẫn cục bộ
        String thumbnailUrl = product.getThumbnail();
        if (!thumbnailUrl.startsWith("http")) {
            // Nếu không phải là URL đầy đủ, thêm BASE_URL vào phía trước
            thumbnailUrl = BASE_URL + thumbnailUrl;
        }

        // Load ảnh từ URL vào ImageView sử dụng Glide
        Glide.with(holder.thumbnail.getContext())
                .load(thumbnailUrl)
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Phương thức để cập nhật danh sách sản phẩm cho một trang mới
    public void updateProductList(List<Product> newProductList) {
        this.productList.clear();
        this.productList.addAll(newProductList);
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, salePrice;
        ImageView thumbnail;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            salePrice = itemView.findViewById(R.id.product_sale_price);
            thumbnail = itemView.findViewById(R.id.product_thumbnail);
        }
    }
}
