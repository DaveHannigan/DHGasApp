
package com.example.dhgas

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.RatingCompat
import android.util.Log
import android.view.*
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dhgas.R.id.jobAddressId
import com.example.dhgas.databinding.ActivityCustomerBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class CustomerActivity : AppCompatActivity(), CustomerFragment.customerInterface {

    val customerArray = ArrayList<Customer>()
    override fun cust(customer: Customer, status: String) {

        var index = -1
        if (status == "delete"){
            for (cust in customerArray){
                if (cust.custId == customer.custId){
                    index = customerArray.indexOf(cust)
                }
            }
            if (index != -1) {
                customerArray.removeAt(index)
            }


        }

       if (status == "update") {
           for (cust in customerArray) {
               if (cust.custId == customer.custId) {
                   cust.title = customer.title
                   cust.custsurname = customer.custsurname
                   cust.custFirstName = customer.custFirstName
                   cust.phone_1 = customer.phone_1
                   cust.phone_2 = customer.phone_2
                   cust.phone_3 = customer.phone_3
                   cust.email_1 = customer.email_1
                   cust.email_2 = customer.email_2
                   cust.email_3 = customer.email_3
                   cust.billing_address_1 = customer.billing_address_1
                   cust.billing_address_2 = customer.billing_address_2
                   cust.billing_address_3 = customer.billing_address_3
                   cust.billing_address_city = customer.billing_address_city
                   cust.billing_address_county = customer.billing_address_county
                   cust.billing_address_postcode = customer.billing_address_postcode
                   customer.jobAddress.also { cust.jobAddress = it }
                   customer.jobAddressId.also { cust.jobAddressId = it }
                   

               }
           }
           customerArray.sortBy { x: Customer -> x.custsurname }
       }
        if (status == "new"){
            customerArray.add(customer)
            customerArray.sortBy { x: Customer -> x.custsurname  }
        }

            val recycle = findViewById<RecyclerView>(R.id.listCustomers)
            val search = findViewById<SearchView>(R.id.searchCustomers)

            displayCustomer(recycle, search, customerArray, this)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.customerToolbar
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)



        val db = FirebaseFirestore.getInstance()
        db.collection("Customer")
            .get()
            .addOnSuccessListener {task->
                for (doclist in task){
                    var customer = Customer(doclist["surname"].toString())
                    customer.custId = doclist.id
                    customer.title = doclist["title"].toString()
                    customer.custFirstName = doclist["first_name"].toString()
                    customer.phone_1 = blank(doclist["phone_1"].toString())
                    customer.email_1 = blank(doclist["email_1"].toString())
                    customer.phone_2 = blank(doclist["phone_2"].toString())
                    customer.email_2 = blank(doclist["email_2"].toString())
                    customer.phone_3 = blank(doclist["phone_3"].toString())
                    customer.email_3 = blank(doclist["email_3"].toString())

                    customer.billing_address_1 = blank(doclist["billing_address_1"].toString())
                    customer.billing_address_2 = blank(doclist["billing_address_2"].toString())
                    customer.billing_address_3 = blank(doclist["billing_address_3"].toString())
                    customer.billing_address_city = blank(doclist["billing_address_city"].toString())
                    customer.billing_address_county = blank(doclist["billing_address_county"].toString())
                    customer.billing_address_postcode = blank(doclist["billing_address_postcode"].toString())
                    customer.jobAddressId = blank(doclist["job_address_id"].toString())
                    if (doclist["job_address"] == null){
                        customer.jobAddress = false
                    }else {
                        customer.jobAddress = doclist["job_address"] as Boolean
                    }


                    customerArray.add(customer)
                    customerArray.sortBy {x: Customer -> x.custsurname  }
                }

                val recycle = binding.listCustomers
                val search = binding.searchCustomers
                displayCustomer(recycle,search,customerArray, this)

            }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.customer_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.newCustomer) {
            val dialog = CustomerFragment()
            val b = Bundle()
            b.putString("surname", "")
            dialog.arguments = b
            dialog.show(this.supportFragmentManager, "frag")
        }
        if (id == R.id.importCustomers) {


            val PICK_TXT_FILE = 2
            fun openFile(pickerInitialUri: Uri) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
                startActivityForResult(intent, PICK_TXT_FILE)
                Log.i("menu", "you clicked to import customers")
            }

            var fileName: Uri = Uri.parse("")
            openFile(fileName)
        }


        return super.onOptionsItemSelected(item)
    }

}

fun displayCustomer(recyle: RecyclerView, search: SearchView, customerArray: ArrayList<Customer>, context: Context){
    val adapter = CustomerAdapter(context, customerArray)
    search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            adapter.filter.filter(newText)
            return false
        }
    })
    val layout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    recyle.layoutManager = layout
    recyle.adapter = adapter
}

fun blank(test: String): String{
    return if(test == "null"){
        ""
    }else{
        test
    }

}

class CustomerAdapter(context: Context, customers: ArrayList<Customer>):
                    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable{
    var context: Context =context
    var customer: ArrayList<Customer> = customers
    var customersFiltered = ArrayList<Customer>()

    init{
        this.customersFiltered = customers
    }



    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()){
                    customersFiltered = customer
                }else{
                    val results = ArrayList<Customer>()
                    for (row in customer){
                        if (row.nameAndAddress().toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT)))
                            results.add(row)
                    }
                    customersFiltered = results
                }
                val  filterResults = FilterResults()
                filterResults.values = customersFiltered
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                customersFiltered = results?.values as ArrayList<Customer>
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflator = LayoutInflater.from(context)
        return CustomerHolder(inflator.inflate(R.layout.customer_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val customer_1 = customersFiltered[position]
        val  ch = holder as CustomerHolder

        ch.customerName.text = customer_1.nameAndAddress()
        ch.phone1.text = customer_1.phone_1
        ch.email1.text = customer_1.email_1
        ch.phone2.text = customer_1.phone_2
        ch.email2.text = customer_1.email_2
        ch.phone3.text = customer_1.phone_3
        ch.email3.text = customer_1.email_3
        ch.surname.text = customer_1.custsurname
        ch.firstName.text = customer_1.custFirstName

        ch.address1.text = customer_1.billing_address_1
        ch.address2.text = customer_1.billing_address_2
        ch.address3.text = customer_1.billing_address_3
        ch.city.text = customer_1.billing_address_city
        ch.county.text = customer_1.billing_address_county
        ch.postcode.text = customer_1.billing_address_postcode
        ch.custId.text = customer_1.custId
        ch.title.text = customer_1.title
        ch.jobAddress.isChecked = customer_1.jobAddress
        ch.jobAddressId.text = customer_1.jobAddressId



        if (position % 2 == 1){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue))
        }else{
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_blue))

        }
    }

    override fun getItemCount(): Int {
        return customersFiltered.size
    }

    internal class CustomerHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var title = itemView.findViewById<TextView>(R.id.title)
        var customerName = itemView.findViewById<TextView>(R.id.customerName)
        var phone1 = itemView.findViewById<TextView>(R.id.phone_1)
        var email1 = itemView.findViewById<TextView>(R.id.email_1)
        var phone2 = itemView.findViewById<TextView>(R.id.phone_2)
        var email2 = itemView.findViewById<TextView>(R.id.email_2)
        var phone3 = itemView.findViewById<TextView>(R.id.phone_3)
        var email3 = itemView.findViewById<TextView>(R.id.email_3)
        var surname = itemView.findViewById<TextView>(R.id.surname)
        var firstName = itemView.findViewById<TextView>(R.id.firstName)

        var address1 = itemView.findViewById<TextView>(R.id.address_1)
        var address2 = itemView.findViewById<TextView>(R.id.address_2)
        var address3 = itemView.findViewById<TextView>(R.id.address_3)
        var city = itemView.findViewById<TextView>(R.id.city)
        var county = itemView.findViewById<TextView>(R.id.county)
        var postcode = itemView.findViewById<TextView>(R.id.postcode)
        var custId = itemView.findViewById<TextView>(R.id.custId)
        var jobAddress = itemView.findViewById<CheckBox>(R.id.checkbox)
        var jobAddressId = itemView.findViewById<TextView>(R.id.jobAddressId)

        init {
            itemView.setOnClickListener{
                val position = itemView.id
                val activity = itemView.context as CustomerActivity
                val dialog = CustomerFragment()
                val b = Bundle()
                b.putString("title", title.text.toString())
                b.putString("surname", surname.text.toString())
                b.putString("firstName", firstName.text.toString())
                b.putString("phone1", phone1.text.toString())
                b.putString("phone2", phone2.text.toString())
                b.putString("phone3", phone3.text.toString())
                b.putString("email1", email1.text.toString())
                b.putString("email2", email2.text.toString())
                b.putString("email3", email3.text.toString())

                b.putString("address1", address1.text.toString())
                b.putString("address2", address2.text.toString())
                b.putString("address3", address3.text.toString())
                b.putString("city", city.text.toString())
                b.putString("county", county.text.toString())
                b.putString("postcode", postcode.text.toString())
                b.putString("custId", custId.text.toString())
                b.putBoolean("jobAddress", jobAddress.isChecked)
                b.putString("jobAddressId", jobAddressId.text.toString())


                dialog.arguments = b
                dialog.show(activity.supportFragmentManager, "frag")
            }
        }

    }


}
@Suppress("DEPRECATION")
class CustomerFragment: DialogFragment(){


    interface customerInterface{
        fun cust(customer: Customer, status: String)
    }
    lateinit var newCustomer: customerInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            newCustomer = context as customerInterface
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val rootView = inflater.inflate(R.layout.fragment_customer, container, false)
        dialog?.window?.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE)

        val db = FirebaseFirestore.getInstance()
        val spinner = rootView.findViewById<Spinner>(R.id.title)
        val cancelButton = rootView.findViewById<Button>(R.id.cancel)
        val deleteButton = rootView.findViewById<Button>(R.id.delete)
        cancelButton?.setOnClickListener { dismiss() }


        val args = arguments
        val surname = args?.getString("surname", "")
        val cust1 = Customer(surname!!)
        val title = args.getString("title", "")
        cust1.title = title
        val firstName = args.getString("firstName", "")
        cust1.custFirstName = firstName
        val phone1 = args.getString("phone1", "")
        cust1.phone_1 = phone1
        val phone2 = args.getString("phone2", "")
        cust1.phone_2 = phone2
        val phone3 = args.getString("phone3", "")
        cust1.phone_3 = phone3
        val email1 = args.getString("email1", "")
        cust1.email_1 = email1
        val email2 = args.getString("email2", "")
        cust1.email_2 = email2
        val email3 = args.getString("email3", "")
        cust1.email_3 = email3
        val address1 = args.getString("address1", "")
        cust1.billing_address_1 = address1
        val address2 = args.getString("address2", "")
        cust1.billing_address_2 = address2
        val address3 = args.getString("address3", "")
        cust1.billing_address_3 = address3
        val city = args.getString("city", "")
        cust1.billing_address_city = city
        val county = args.getString("county", "")
        cust1.billing_address_county = county
        val postcode = args.getString("postcode", "")
        cust1.billing_address_postcode = postcode
        val custId = args.getString("custId", "")
        cust1.custId = custId
        val jobAddress = args.getBoolean("jobAddress", false)
        cust1.jobAddress = jobAddress
        val jobAddressId = args.getString("jobAddressId", "")
        Log.i("saveAdd", "job address id is $jobAddressId")

        val titles = resources.getStringArray(R.array.title)
        val index = titles.indexOf(title)
        var customerAltered = false
        var customerBillingAddressAltered = false

        ArrayAdapter.createFromResource(context as Context, R.array.title, android.R.layout.simple_spinner_item)
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    spinner.adapter = adapter
                    if (index == -1){
                        spinner.setSelection(0)
                    }else{
                        spinner.setSelection(index)
                    }
                }

        val surnameSet = rootView.findViewById<EditText>(R.id.surname)
        surnameSet.setText(surname)
        val firstNameSet = rootView.findViewById<EditText>(R.id.firstName)
        firstNameSet.setText(firstName)
        val phone1Set = rootView.findViewById<EditText>(R.id.phone_1)
        phone1Set.setText(phone1)
        val phone2Set = rootView.findViewById<EditText>(R.id.phone_2)
        phone2Set.setText(phone2)
        val phone3Set = rootView.findViewById<EditText>(R.id.phone_3)
        phone3Set.setText(phone3)
        val email1Set = rootView.findViewById<EditText>(R.id.email_1)
        email1Set.setText(email1)
        val email2Set = rootView.findViewById<EditText>(R.id.email_2)
        email2Set.setText(email2)
        val email3Set = rootView.findViewById<EditText>(R.id.email_3)
        email3Set.setText(email3)
        val address1Set = rootView.findViewById<EditText>(R.id.address_1)
        address1Set.setText(address1)
        val address2Set = rootView.findViewById<EditText>(R.id.address_2)
        address2Set.setText(address2)
        val address3Set = rootView.findViewById<EditText>(R.id.address_3)
        address3Set.setText(address3)
        val citySet = rootView.findViewById<EditText>(R.id.city)
        citySet.setText(city)
        val countySet = rootView.findViewById<EditText>(R.id.county)
        countySet.setText(county)
        val postcodeSet = rootView.findViewById<EditText>(R.id.postcode)
        postcodeSet.setText(postcode)
        val checkboxSet = rootView.findViewById<CheckBox>(R.id.checkbox)
        checkboxSet.isChecked = jobAddress
        val jobAddressIdSet = rootView.findViewById<TextView>(R.id.jobAddressId)
        Log.i("saveAdd", "jobAddressIdSet is $jobAddressIdSet")


        deleteButton.setOnClickListener {

            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage("Delete customer ${cust1.fullName()}?")
                    .setCancelable(true)
                    .setPositiveButton("Yes") { dialog, id ->
                        db.collection("Customer").document(cust1.custId)
                                .delete()

                        newCustomer.cust(cust1, "delete")
                    }
                    .setNegativeButton("No") { dialog, which -> dialog.cancel() }
            val alert = dialogBuilder.create()
            alert.setTitle("Delete Customer")
            alert.show()
            dismiss()
        }

        val save = rootView.findViewById<Button>(R.id.save)
        save.setOnClickListener {
            val data = hashMapOf<String, Any>()
            if (custId == ""){
                data["title"] = spinner.selectedItem.toString().trim()
                cust1.title = spinner.selectedItem.toString().trim()

                data["surname"] = surnameSet.text.toString()
                cust1.custsurname = surnameSet.text.toString()

                data["first_name"] = firstNameSet.text.toString().trim()
                cust1.custFirstName = data["first_name"].toString()

                data["phone_1"] = phone1Set.text.toString().trim()
                cust1.phone_1 = phone1Set.text.toString().trim()

                data["phone_2"] = phone2Set.text.toString().trim()
                cust1.phone_2 = phone2Set.text.toString().trim()

                data["phone_3"] = phone3Set.text.toString().trim()
                cust1.phone_3 = phone3Set.text.toString().trim()

                data["email_1"] = email1Set.text.toString().trim()
                cust1.email_1 = email1Set.text.toString().trim()

                data["email_2"] = email2Set.text.toString().trim()
                cust1.email_2 = email2Set.text.toString().trim()

                data["email_3"] = email3Set.text.toString().trim()
                cust1.email_3 = email3Set.text.toString().trim()

                data["billing_address_1"] = address1Set.text.toString().trim()
                cust1.billing_address_1 = address1Set.text.toString().trim()

                data["billing_address_2"] = address2Set.text.toString().trim()
                cust1.billing_address_2 = address2Set.text.toString().trim()

                data["billing_address_3"] = address3Set.text.toString().trim()
                cust1.billing_address_3 = address3Set.text.toString().trim()

                data["billing_address_city"] = citySet.text.toString().trim()
                cust1.billing_address_city = citySet.text.toString().trim()

                data["billing_address_county"] = countySet.text.toString().trim()
                cust1.billing_address_county = countySet.text.toString().trim()

                data["billing_address_postcode"] = postcodeSet.text.toString().trim()
                cust1.billing_address_postcode = postcodeSet.text.toString().trim()

                data["job_address"] = checkboxSet.isChecked
                cust1.jobAddress = checkboxSet.isChecked

                data["job_address_id"] = blank(jobAddressIdSet.text.toString()).also { cust1.jobAddressId = it }
                //cust1.jobAddressId = jobAddressIdSet.text.toString()

                if (checkboxSet.isChecked){
                    val dataAddress1 = hashMapOf<String, Any>(
                            "customer" to cust1.custId,
                            "address_1" to cust1.billing_address_1,
                            "address_2" to cust1.billing_address_2,
                            "address_3" to cust1.billing_address_3,
                            "city" to cust1.billing_address_city,
                            "county" to cust1.billing_address_county,
                            "postcode" to cust1.billing_address_postcode
                    )
                    Log.i("saveAdd", "Adress should now save")
                    db.collection("JobAddress")
                            .add(dataAddress1)
                            .addOnSuccessListener { docRef -> cust1.jobAddressId = docRef.id
                                data["job_address_id"] = docRef.id
                                db.collection("Customer")
                                        .add(data)
                                        .addOnSuccessListener { docRef -> dataAddress1["customer"] = docRef.id
                                        db.collection("JobAddress").document(cust1.jobAddressId)
                                                .update(dataAddress1)}

                                Log.i("saveA", "address id id ${docRef.id}")
                                newCustomer.cust(cust1, "new")
                            }
                }
/*
                db.collection("Customer")
                        .add(data)
                        .addOnSuccessListener { documentReference -> cust1.custId = documentReference.id
                            Log.i("saveAdd", "checkbox is ${checkboxSet.isChecked}")
                        }

 */
                dismiss()

            }else {
                if (title != spinner.selectedItem.toString()) {
                    data["title"] = spinner.selectedItem.toString().trim()
                    cust1.title = data["title"].toString()
                    customerAltered = true
                }
                if (surname != surnameSet.text.toString().trim()) {
                    cust1.custsurname = surnameSet.text.toString().trim()
                    data["surname"] = cust1.custsurname
                    customerAltered = true
                }
                if (firstName != firstNameSet.text.toString().trim()) {
                    data["first_name"] = firstNameSet.text.toString().trim()
                    cust1.custFirstName = data["first_name"].toString()
                    customerAltered = true
                }
                if (phone1 != phone1Set.text.toString().trim()) {
                    data["phone_1"] = phone1Set.text.toString().trim()
                    cust1.phone_1 = phone1Set.text.toString().trim()
                    customerAltered = true
                }
                if (phone2 != phone2Set.text.toString().trim()) {
                    data["phone_2"] = phone2Set.text.toString().trim()
                    cust1.phone_2 = phone2Set.text.toString().trim()
                    customerAltered = true
                }
                if (phone3 != phone3Set.text.toString().trim()) {
                    data["phone_3"] = phone3Set.text.toString().trim()
                    cust1.phone_3 = phone3Set.text.toString().trim()
                    customerAltered = true
                }
                if (email1 != email1Set.text.toString().trim()) {
                    data["email_1"] = email1Set.text.toString().trim()
                    cust1.email_1 = email1Set.text.toString().trim()
                    customerAltered = true
                }
                if (email2 != email2Set.text.toString().trim()) {
                    data["email_2"] = email2Set.text.toString().trim()
                    cust1.email_2 = email2Set.text.toString().trim()
                    customerAltered = true
                }
                if (email3 != email3Set.text.toString().trim()) {
                    data["email_3"] = email3Set.text.toString().trim()
                    cust1.email_3 = email3Set.text.toString().trim()
                    customerAltered = true
                }
                if (address1 != address1Set.text.toString().trim()) {
                    data["billing_address_1"] = address1Set.text.toString().trim()
                    cust1.billing_address_1 = address1Set.text.toString().trim()
                    customerBillingAddressAltered = true
                }
                if (address2 != address2Set.text.toString().trim()) {
                    data["billing_address_2"] = address2Set.text.toString().trim()
                    cust1.billing_address_2 = address2Set.text.toString().trim()
                    customerBillingAddressAltered = true
                }
                if (address3 != address3Set.text.toString().trim()) {
                    data["billing_address_3"] = address3Set.text.toString().trim()
                    cust1.billing_address_3 = address3Set.text.toString().trim()
                    customerBillingAddressAltered = true
                }
                if (city != citySet.text.toString().trim()) {
                    data["billing_address_city"] = citySet.text.toString().trim()
                    cust1.billing_address_city = citySet.text.toString().trim()
                    customerBillingAddressAltered = true
                }
                if (county != countySet.text.toString().trim()) {
                    data["billing_address_county"] = countySet.text.toString().trim()
                    cust1.billing_address_county = countySet.text.toString().trim()
                    customerBillingAddressAltered = true
                }
                if (postcode != postcodeSet.text.toString().trim()) {
                    data["billing_address_postcode"] = postcodeSet.text.toString().trim()
                    cust1.billing_address_postcode = postcodeSet.text.toString().trim()
                    customerBillingAddressAltered = true
                }

                if (jobAddress != checkboxSet.isChecked){
                    data["job_address"] = checkboxSet.isChecked
                    cust1.jobAddress = checkboxSet.isChecked
                    customerAltered = true
                }
            }

                if (custId != "" && !customerAltered){
                    dismiss()
                    return@setOnClickListener
                }

                    Log.i("saveAdd", "cust id is $custId & customer altered is $customerAltered")

                if (custId != "" && customerAltered) {
                    Log.i("saveAdd", "in if()")
                    db.collection("Customer").document(custId)
                            .update(data)
                    newCustomer.cust(cust1, "update")
                    Log.i("saveAdd", "checkbox is ${checkboxSet.isChecked} and !cust1.jobAddress is " +
                            "${!jobAddress}")
                    if(checkboxSet.isChecked && !jobAddress){
                        Log.i("saveAddress", "value of cust add 2 is ${cust1.billing_address_2}")
                        val dataAddress = hashMapOf<String, Any>(
                                "customer" to cust1.custId,
                                "address_1" to cust1.billing_address_1,
                                "address_2" to cust1.billing_address_2,
                                "address_3" to cust1.billing_address_3,
                                "city" to cust1.billing_address_city,
                                "county" to cust1.billing_address_county,
                                "postcode" to cust1.billing_address_postcode
                        )
                        db.collection("JobAddress").add(dataAddress)
            Log.i("saveAddress", "you chose to save address")

                    }

                }else{

                }
                dismiss()



        }
        fun saveAddress(address: ArrayList<String>){
        }
    return rootView
    }


}