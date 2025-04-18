package com.example.demo.controllers;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.models.Expense;
import com.example.demo.models.Trip;
import com.example.demo.services.CalculationService;
import com.example.demo.services.ExpenseService;
import com.example.demo.services.TripService;

@Controller
public class TripController {
    @Autowired
    private TripService tripService;

    @Autowired
    private CalculationService calculationService;
    
    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/trips")					// get all trips
    public String getAllTrips(Model model) {
        List<Trip> trips = tripService.getAllTrips();
        model.addAttribute("trips", trips);
        return "tripList";
    }

    @GetMapping("/trip/new")				// create new Trip
    public String showTripForm(Model model) {
        model.addAttribute("trip", new Trip());
        return "tripForm";
    }

    @PostMapping("/trip/save")				// save new trip
    public String saveTrip(@ModelAttribute("trip") Trip trip) {
        tripService.saveTrip(trip);
        return "redirect:/trips";
    }

    @GetMapping("/trip/edit/{id}")			// Edit trip information
    public String editTrip(@PathVariable String id, Model model) {
        Trip trip = tripService.getTripById(id);
        model.addAttribute("trip", trip);
        return "tripForm";
    }

    @GetMapping("/trip/delete/{id}")		// delete trip from database
    public String deleteTrip(@PathVariable String id) {
        tripService.deleteTrip(id);
        return "redirect:/trips";
    }

    @GetMapping("/trip/{tripId}/expenses")				// View expense of particular trip
    public String viewExpenses(@PathVariable String tripId, Model model) {
        Trip trip = tripService.getTripById(tripId);
        List<Expense> expenses = expenseService.getExpensesByTripId(tripId);
        double totalAmount = expenses.stream().mapToDouble(Expense::getContributeAmount).sum();
        model.addAttribute("trip", trip);
        model.addAttribute("expenses", expenses);
        model.addAttribute("totalAmount", totalAmount);
        return "expenseList";
    }
    
    @GetMapping("/trip/{tripId}/expense/new")			// add new expense in trip
    public String showExpenseForm(@PathVariable String tripId, Model model) {
        Expense expense = new Expense();
        expense.setTrip(tripService.getTripById(tripId));
        model.addAttribute("expense", expense);
        return "expenseForm";
    }

    @PostMapping("/trip/{tripId}/expense/save")			// save the new expense of trip
    public String saveExpense(@PathVariable String tripId, @ModelAttribute("expense") Expense expense) {
        expense.setTrip(tripService.getTripById(tripId));
        expenseService.saveExpense(expense);
        return "redirect:/trip/" + tripId + "/expenses";
    }

    @GetMapping("/trip/{tripId}/expense/edit/{expenseId}")		// edit the expense information of particular trip
    public String editExpense(@PathVariable String tripId, @PathVariable String expenseId, Model model) {
        Expense expense = expenseService.getExpenseById(expenseId);
        model.addAttribute("expense", expense);
        return "expenseForm";
    }

    @GetMapping("/trip/{tripId}/expense/delete/{expenseId}")		// delete the expense from particular trip from database as well
    public String deleteExpense(@PathVariable String tripId, @PathVariable String expenseId) {
        expenseService.deleteExpense(expenseId);
        return "redirect:/trip/" + tripId + "/expenses";
    }

    @GetMapping("/trip/{tripId}/expense-summary")				// show the expense summary of particular trip
    public String showExpenseSummary(@PathVariable String tripId, Model model) {
        Trip trip = tripService.getTripById(tripId);
        Map<String, Map<String, Double>> expenseSummary = calculationService.calculateExpenseShare(trip);
        model.addAttribute("trip", trip);
        model.addAttribute("expenseSummary", expenseSummary);
        return "expenseSummary";
    }
    
    @GetMapping("/trip/{tripId}/generateUpiLink")
    @ResponseBody
    public String generateUpiLink(
            @RequestParam String upiId,
            @RequestParam String payeeName,
            @RequestParam String amount,
            @RequestParam(defaultValue = "Expense Payment") String transactionNote) {

        // Construct the UPI deep link
        String upiLink = String.format(
                "upi://pay?pa=%s&pn=%s&am=%s&cu=INR&tn=%s",
                upiId, payeeName, amount, transactionNote);

        return upiLink;
    }
    
}