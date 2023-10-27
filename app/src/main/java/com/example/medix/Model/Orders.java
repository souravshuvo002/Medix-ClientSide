package com.example.medix.Model;

public class Orders {

    private String id_order;
    private String name;
    private String phone;
    private String address;
    private String total_price;


    //place order
    private String invoice_no, invoice_prefix, store_id, store_name, store_url, customer_id, customer_group_id, firstname, lastname,
            email, telephone, fax, custom_field, payment_firstname, payment_lastname, payment_company, payment_address_1, payment_address_2,
            payment_city, payment_postcode, payment_country, payment_country_id, payment_zone, payment_zone_id, payment_address_format,
            payment_custom_field, payment_method, payment_code, shipping_firstname, shipping_lastname, shipping_company, shipping_address_1,
            shipping_address_2, shipping_city, shipping_postcode, shipping_country, shipping_country_id, shipping_zone, shipping_zone_id,
            shipping_address_format, shipping_custom_field, shipping_method, shipping_code, comment, total, order_status_id, affiliate_id,
            commission, marketing_id, tracking, language_id, currency_id, currency_code, currency_value, ip, forwarded_ip, user_agent, accept_language,
            date_added, date_modified, status_name, product_total, model, quantity, price, image, category_id, product_id, cat_name, product_name;

    private String seller_id;
    private String gen_status_name, vendor_order_status_id;

    private int order_id;



    public Orders() {
    }

    public Orders(String id_order, String name, String phone, String address, String total_price) {
        this.id_order = id_order;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.total_price = total_price;
    }

    public String getId_order() {
        return id_order;
    }

    public void setId_order(String id_order) {
        this.id_order = id_order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public String getInvoice_prefix() {
        return invoice_prefix;
    }

    public void setInvoice_prefix(String invoice_prefix) {
        this.invoice_prefix = invoice_prefix;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_url() {
        return store_url;
    }

    public void setStore_url(String store_url) {
        this.store_url = store_url;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_group_id() {
        return customer_group_id;
    }

    public void setCustomer_group_id(String customer_group_id) {
        this.customer_group_id = customer_group_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getCustom_field() {
        return custom_field;
    }

    public void setCustom_field(String custom_field) {
        this.custom_field = custom_field;
    }

    public String getPayment_firstname() {
        return payment_firstname;
    }

    public void setPayment_firstname(String payment_firstname) {
        this.payment_firstname = payment_firstname;
    }

    public String getPayment_lastname() {
        return payment_lastname;
    }

    public void setPayment_lastname(String payment_lastname) {
        this.payment_lastname = payment_lastname;
    }

    public String getPayment_company() {
        return payment_company;
    }

    public void setPayment_company(String payment_company) {
        this.payment_company = payment_company;
    }

    public String getPayment_address_1() {
        return payment_address_1;
    }

    public void setPayment_address_1(String payment_address_1) {
        this.payment_address_1 = payment_address_1;
    }

    public String getPayment_address_2() {
        return payment_address_2;
    }

    public void setPayment_address_2(String payment_address_2) {
        this.payment_address_2 = payment_address_2;
    }

    public String getPayment_city() {
        return payment_city;
    }

    public void setPayment_city(String payment_city) {
        this.payment_city = payment_city;
    }

    public String getPayment_postcode() {
        return payment_postcode;
    }

    public void setPayment_postcode(String payment_postcode) {
        this.payment_postcode = payment_postcode;
    }

    public String getPayment_country() {
        return payment_country;
    }

    public void setPayment_country(String payment_country) {
        this.payment_country = payment_country;
    }

    public String getPayment_country_id() {
        return payment_country_id;
    }

    public void setPayment_country_id(String payment_country_id) {
        this.payment_country_id = payment_country_id;
    }

    public String getPayment_zone() {
        return payment_zone;
    }

    public void setPayment_zone(String payment_zone) {
        this.payment_zone = payment_zone;
    }

    public String getPayment_zone_id() {
        return payment_zone_id;
    }

    public void setPayment_zone_id(String payment_zone_id) {
        this.payment_zone_id = payment_zone_id;
    }

    public String getPayment_address_format() {
        return payment_address_format;
    }

    public void setPayment_address_format(String payment_address_format) {
        this.payment_address_format = payment_address_format;
    }

    public String getPayment_custom_field() {
        return payment_custom_field;
    }

    public void setPayment_custom_field(String payment_custom_field) {
        this.payment_custom_field = payment_custom_field;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getPayment_code() {
        return payment_code;
    }

    public void setPayment_code(String payment_code) {
        this.payment_code = payment_code;
    }

    public String getShipping_firstname() {
        return shipping_firstname;
    }

    public void setShipping_firstname(String shipping_firstname) {
        this.shipping_firstname = shipping_firstname;
    }

    public String getShipping_lastname() {
        return shipping_lastname;
    }

    public void setShipping_lastname(String shipping_lastname) {
        this.shipping_lastname = shipping_lastname;
    }

    public String getShipping_company() {
        return shipping_company;
    }

    public void setShipping_company(String shipping_company) {
        this.shipping_company = shipping_company;
    }

    public String getShipping_address_1() {
        return shipping_address_1;
    }

    public void setShipping_address_1(String shipping_address_1) {
        this.shipping_address_1 = shipping_address_1;
    }

    public String getShipping_address_2() {
        return shipping_address_2;
    }

    public void setShipping_address_2(String shipping_address_2) {
        this.shipping_address_2 = shipping_address_2;
    }

    public String getShipping_city() {
        return shipping_city;
    }

    public void setShipping_city(String shipping_city) {
        this.shipping_city = shipping_city;
    }

    public String getShipping_postcode() {
        return shipping_postcode;
    }

    public void setShipping_postcode(String shipping_postcode) {
        this.shipping_postcode = shipping_postcode;
    }

    public String getShipping_country() {
        return shipping_country;
    }

    public void setShipping_country(String shipping_country) {
        this.shipping_country = shipping_country;
    }

    public String getShipping_country_id() {
        return shipping_country_id;
    }

    public void setShipping_country_id(String shipping_country_id) {
        this.shipping_country_id = shipping_country_id;
    }

    public String getShipping_zone() {
        return shipping_zone;
    }

    public void setShipping_zone(String shipping_zone) {
        this.shipping_zone = shipping_zone;
    }

    public String getShipping_zone_id() {
        return shipping_zone_id;
    }

    public void setShipping_zone_id(String shipping_zone_id) {
        this.shipping_zone_id = shipping_zone_id;
    }

    public String getShipping_address_format() {
        return shipping_address_format;
    }

    public void setShipping_address_format(String shipping_address_format) {
        this.shipping_address_format = shipping_address_format;
    }

    public String getShipping_custom_field() {
        return shipping_custom_field;
    }

    public void setShipping_custom_field(String shipping_custom_field) {
        this.shipping_custom_field = shipping_custom_field;
    }

    public String getShipping_method() {
        return shipping_method;
    }

    public void setShipping_method(String shipping_method) {
        this.shipping_method = shipping_method;
    }

    public String getShipping_code() {
        return shipping_code;
    }

    public void setShipping_code(String shipping_code) {
        this.shipping_code = shipping_code;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getOrder_status_id() {
        return order_status_id;
    }

    public void setOrder_status_id(String order_status_id) {
        this.order_status_id = order_status_id;
    }

    public String getAffiliate_id() {
        return affiliate_id;
    }

    public void setAffiliate_id(String affiliate_id) {
        this.affiliate_id = affiliate_id;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getMarketing_id() {
        return marketing_id;
    }

    public void setMarketing_id(String marketing_id) {
        this.marketing_id = marketing_id;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getCurrency_value() {
        return currency_value;
    }

    public void setCurrency_value(String currency_value) {
        this.currency_value = currency_value;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getForwarded_ip() {
        return forwarded_ip;
    }

    public void setForwarded_ip(String forwarded_ip) {
        this.forwarded_ip = forwarded_ip;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    public String getAccept_language() {
        return accept_language;
    }

    public void setAccept_language(String accept_language) {
        this.accept_language = accept_language;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public String getProduct_total() {
        return product_total;
    }

    public void setProduct_total(String product_total) {
        this.product_total = product_total;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getGen_status_name() {
        return gen_status_name;
    }

    public void setGen_status_name(String gen_status_name) {
        this.gen_status_name = gen_status_name;
    }

    public String getVendor_order_status_id() {
        return vendor_order_status_id;
    }

    public void setVendor_order_status_id(String vendor_order_status_id) {
        this.vendor_order_status_id = vendor_order_status_id;
    }
}