package com.cs4520.assignment5.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs4520.assignment5.R
import com.cs4520.assignment5.databinding.ProductListFragmentBinding
import com.cs4520.assignment5.databinding.ProductListItemBinding
import com.cs4520.assignment5.logic.CategorizedProduct
import com.cs4520.assignment5.logic.DisplayProducts
import com.cs4520.assignment5.logic.ProductsViewModel

/**
 * The fragment displaying the list of products.
 */
class ProductListFragment : Fragment() {
    private lateinit var binding: ProductListFragmentBinding
    private lateinit var viewModel: ProductsViewModel
    private lateinit var rvAdapter:
            UpdatableRecyclerViewAdapter<RecyclerViewAdapter.ViewHolder, CategorizedProduct>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ProductListFragmentBinding.inflate(inflater).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ProductsViewModel::class.java]

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        rvAdapter = RecyclerViewAdapter()
        binding.recyclerView.adapter = rvAdapter

        viewModel.displayProducts.observe(viewLifecycleOwner, ::onDisplayProductsChanged)

        viewModel.loadProductData()
    }

    // Updates the view based on the given product data (or lack thereof)
    private fun onDisplayProductsChanged(displayProducts: DisplayProducts) {
        hideAllComponents()

        when (displayProducts) {
            is DisplayProducts.ServerError -> {
                binding.errorTextView.visibility = View.VISIBLE
            }
            is DisplayProducts.ServerNoProducts -> {
                binding.serverNoProductsTextView.visibility = View.VISIBLE
            }
            is DisplayProducts.OfflineNoProducts -> {
                binding.offlineNoProductsTextView.visibility = View.VISIBLE
            }
            is DisplayProducts.ProductList -> {
                rvAdapter.updateItems(displayProducts.products)
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
    }

    // Makes all components on the fragment invisible
    private fun hideAllComponents() {
        with (binding) {
            progressBar.visibility = View.INVISIBLE
            recyclerView.visibility = View.INVISIBLE
            errorTextView.visibility = View.INVISIBLE
            serverNoProductsTextView.visibility = View.INVISIBLE
        }
    }
}

/**
 * Represents a RecyclerView.Adapter that can be updated with new items.
 */
private abstract class UpdatableRecyclerViewAdapter<VH : RecyclerView.ViewHolder, ItemType> :
    RecyclerView.Adapter<VH>() {

    /**
     * Provides a new list of items to display, and automatically updates the recycler view to
     * display these items.
     */
    abstract fun updateItems(newItems: List<ItemType>)
}

/**
 * The adapter for the RecyclerView displaying the list of products.
 */
private class RecyclerViewAdapter :
    UpdatableRecyclerViewAdapter<RecyclerViewAdapter.ViewHolder, CategorizedProduct>() {

    private var products: List<CategorizedProduct> = listOf()

    /**
     * The ViewHolder for each item in the RecyclerView.
     */
    class ViewHolder(val view: View, val binding: ProductListItemBinding) :
        RecyclerView.ViewHolder(view) {

        /**
         * Updates the view to represent the given product's information.
         */
        fun bind(product: CategorizedProduct) {
            with (binding) {
                when (product) {
                    is CategorizedProduct.Equipment -> {
                        productImage.setImageResource(R.drawable.equipment)
                        constraintLayout.setBackgroundResource(R.color.product_equipment_background)
                    }
                    is CategorizedProduct.Food -> {
                        productImage.setImageResource(R.drawable.food)
                        constraintLayout.setBackgroundResource(R.color.product_food_background)
                    }
                }

                productName.text = product.name

                if (product.expiryDate == null) {
                    productExpiryDate.visibility = View.GONE
                } else {
                    productExpiryDate.visibility = View.VISIBLE
                    productExpiryDate.text = product.expiryDate
                }

                productPrice.text = view.context.getString(
                    R.string.product_price_text, product.price
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ProductListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false).let {
            return ViewHolder(it.root, it)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    override fun updateItems(newItems: List<CategorizedProduct>) {
        products = newItems
        notifyDataSetChanged()
    }
}