package controller;

import dal.AccountDAO;
import dal.ContractDAO;
import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Account;
import model.Contract;
import model.User;

/**
 *
 * @author admin
 */
public class LoginServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoginServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("Login.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        AccountDAO db = new AccountDAO();
        Account a = db.getAccount(username, password);

        // n???u t??i kho???n kh??ng t???n t???i
        if (a == null) {
            request.setAttribute("error", "T??i kho???n " + username + " kh??ng t???n t???i");
            request.getRequestDispatcher("Login.jsp").forward(request, response);
        } 
        
        // n???u t??i kho???n b??? ban th?? kh??ng ???????c v??o
        else if (!a.isActive()){
            request.setAttribute("error", "T??i kho???n " + username + " ???? b??? kh??a");
            request.getRequestDispatcher("Login.jsp").forward(request, response);
        }

        // n???u t???n t???i t??i kho???n
        else {
            HttpSession session = request.getSession();

            // l???y data ng?????i d??ng t??? db
            UserDAO adb = new UserDAO();
            User cus = adb.getCustomerInfo(a);
            session.setAttribute("user", cus);
            
            

            // l???y data contract t??? db
            ContractDAO cdb = new ContractDAO();
            Contract contract = cdb.getContractInfo(username);
            if (contract != null) {
                session.setAttribute("contract", contract);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date now = new Date();
                Date end = null;
                try {
                    end = sdf.parse(contract.getEndDate());
                } catch (ParseException ex) {
                    Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (!now.before(end)) {
                    adb.downgrade(cus.getUsername());
                }
            }
            
            // l???y data v??? account ?????t l??n session
            session.setAttribute("account", a);
            if (a.getRole().equals("admin")) {
                response.sendRedirect("acrud");
            } else {
                response.sendRedirect("home");
            }
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
