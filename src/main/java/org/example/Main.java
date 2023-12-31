package org.example;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/sakila";
        String username = "root";
        String password = "example";

        Connection con = DriverManager.getConnection(url, username, password);

        // ---------------------------------

        String firstQuery = """
            SELECT first_name, last_name
            FROM actor
            
            INNER JOIN
            (SELECT actor_id, count(*) as c
             FROM film_actor
             GROUP BY actor_id
             ORDER BY c DESC
             LIMIT 10
             ) AS most_appearances
            
            ON actor.actor_id = most_appearances.actor_id;
            """;
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(firstQuery);

        System.out.println("\nFirst query:");
        while (rs.next()) {
            System.out.println(rs.getString("first_name") + " " + rs.getString("last_name"));
        }

        // ---------------------------------

        String category = "Comedy";  // pretend we got this as input from user
        String secondQuery = """
            SELECT *
            FROM film_category
            
            INNER JOIN
            
            (SELECT category_id
             FROM category
             WHERE name = ?
             ) AS selected_category
            
            ON film_category.category_id = selected_category.category_id;
            """;
        PreparedStatement pst = con.prepareStatement(secondQuery);
        pst.setString(1, category);
        rs = pst.executeQuery();

        System.out.println("\nSecond query:");
        while (rs.next()) {
            System.out.println(rs.getString("film_id"));
        }

        // ---------------------------------

        int id = 12321;  // pretend we got this as input from user
        String thirdQuery = """
            REPLACE INTO customer
            VALUES (
                ?,
                1,
                "Josefina",
                "Holland",
                "josefina.holland@sakilacustomer.org",
                3,
                1,
                "2006-02-14 22:04:36",
                "2006-02-14 22:04:36"
            );
            """;
        pst = con.prepareStatement(thirdQuery);
        pst.setInt(1, id);

        System.out.println("\nThird query:");
        try {
            pst.execute();
            System.out.println("Updated row with id " + id);
        } catch (Exception e) {
            System.out.println("Cannot update row with id " + id);
        }

        // ---------------------------------

        String email = "exercise@mdas.com";  // pretend we got this as input from user
        String fourthQuery = """
            UPDATE customer
            SET email = ?
            WHERE customer_id = ?;
            """;
        pst = con.prepareStatement(fourthQuery);
        pst.setString(1, email);
        pst.setInt(2, id);

        System.out.println("\nFourth query:");
        try {
            pst.execute();
            System.out.println("Updated row with id " + id);
        } catch (Exception e) {
            System.out.println("Cannot update row with id " + id);
        }

        // ---------------------------------

        String fifthQuery = """
            SELECT *
            FROM customer
            
            INNER JOIN
            (SELECT MAX(customer_id) AS customer_id, MAX(create_date) as added_to_store
             FROM customer
             GROUP BY store_id
             ) AS added_customer
             
            ON customer.customer_id = added_customer.customer_id;
            """;
        st = con.createStatement();
        rs = st.executeQuery(fifthQuery);

        System.out.println("\nFifth query:");
        while (rs.next()) {
            System.out.println(
                rs.getString("customer_id") + " | " +
                rs.getString("store_id") + " | " +
                rs.getString("first_name") + " | " +
                rs.getString("last_name") + " | " +
                rs.getString("email") + " | " +
                rs.getString("address_id") + " | " +
                rs.getString("active") + " | " +
                rs.getString("create_date") + " | " +
                rs.getString("last_update") + " | " +
                rs.getString("added_to_store")
            );
        }

        // ---------------------------------

        con.close();
    }
}