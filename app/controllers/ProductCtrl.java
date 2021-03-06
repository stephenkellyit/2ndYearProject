package controllers;

import play.api.Environment;
import play.mvc.*;
import play.data.*;
import play.db.ebean.Transactional;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import views.html.*;

// Import models
import models.users.*;
import models.products.*;


public class ProductCtrl extends Controller {

    /** Dependency Injection **/

    /** http://stackoverflow.com/questions/15600186/play-framework-dependency-injection **/
    private FormFactory formFactory;

    /** http://stackoverflow.com/a/37024198 **/
    private play.api.Environment env;

    /** http://stackoverflow.com/a/10159220/6322856 **/
    @Inject
    public ProductCtrl(Environment e, FormFactory f) {
        this.env = e;
        this.formFactory = f;
    }

    // Get a user - if logged in email will be set in the session
    @Transactional
	public User getCurrentUser() {
		User u = User.getLoggedIn(session().get("email"));
		return u;
	}

    public Result index() {
                 User u = getCurrentUser();


             if (u != null){
                if (u.getRole().equalsIgnoreCase("admin")){
                return redirect(routes.AdminProductCtrl.list(0, "", "", "", 0));
		}          	
	     }
		return redirect(routes.ProductCtrl.list(0, "", "", "", 0));

    }

	// Get a list of products
    // If cat parameter is 0 then return all products
    // Otherwise return products for a category (by id)
    // In both cases products will be searched using the fiter value
    @Transactional
    public Result listProducts(Long cat, String filter) {
        // Get list of all categories in ascending order
        List<Category> categories = Category.findAll();
        // Instantiate products, an Array list of products			
        List<Product> products = new ArrayList<Product>();
    
        if (cat == 0) {
            // Get the list of ALL products with filter
            products = Product.findAll(filter);
        }
        else {
            // Get products for the selected category and filter (search field)
            products = Product.findFilter(cat, filter);
        }
        // Render the list products view, passing parameters
        // categories and products lists
        // category id - used for filtering
        // the filter string - this will be displayed in the filter text input
        // current user - if one is logged in
        return ok(listProducts.render(env, categories, products, cat, filter, getCurrentUser()));
    }

 /**
     * Display the paginated list of computers.
     *
     * @param page Current page number (starts from 0)
     * @param sortBy Column to be sorted
     * @param order Sort order (either asc or desc)
     * @param filter Filter applied on computer names
     */
    public Result list(int page, String sortBy, String order, String filter, Long cat) {
       List<Category> categories = Category.findAll();
        return ok(
            productsPageSpilt.render(
                env, categories, Product.page( page, 10, sortBy, order, filter, cat),
                sortBy, order, filter, cat, getCurrentUser()
            )
        );
    }

   public Result listCat(int page, String sortBy, String order, String filter, Long cat) {
       List<Category> categories = Category.findAll();
        return ok(
            productsPageSpilt.render(
                env, categories, Product.pageCat( page, 10, sortBy, order, filter, cat),
                sortBy, order, filter, cat, getCurrentUser()
            )
        );
    }

    

    

    @Transactional
    public Result product(Long id) {
         // Retrieve the product by id
        Product p = Product.find.byId(id);

        List<Review> reviews = p.getReviews();
    

        return ok(product.render(env, p, reviews, User.getLoggedIn(session().get("email"))));
    }
     


}
