package com.example.demo.services;

import java.math.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.models.Member;
import com.example.demo.models.Trip;
import com.example.demo.repository.MemberRepo;
import com.example.demo.repository.TripRepo;

@Service
public class TripService {
    @Autowired
    private TripRepo tripRepository;

    @Autowired
    private MemberRepo memberRepository; 

    // Save a trip
    public Trip saveTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    // Save a member
    public Member saveMember(Member member) {
        return memberRepository.save(member); 
    }

    public Trip getTripById(String id) {
        return tripRepository.findById(id).orElse(null);
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }
    public void deleteTrip(String id) {
        tripRepository.deleteById(id);
    }

//    public Trip updateTrip(String id, Trip trip) {
//    	Optional<Trip> t = tripRepository.findById(id);
//    	Trip t1 = t.get();
//    	t1.setTripName(trip.getTripName());
//    	t1.setStartDate(trip.getStartDate());
//    	t1.setEndDate(trip.getEndDate());
//    	t1.setDestination(trip.getDestination());
//    	t1.setBudget(trip.getBudget());
//    	t1.setTripDescription(trip.getTripDescription());
//    	tripRepository.save(t1);
//        return t1;
//    }
    
    @SuppressWarnings("deprecation")
	public Map<Member, BigDecimal> calculateDistribution(List<Member> members) {
        Map<Member, BigDecimal> balanceMap = new HashMap<>();
        for (Member member : members) {
            BigDecimal total = member.getTotalContribution();
            BigDecimal count = BigDecimal.valueOf(members.size());  
            BigDecimal balance;
            if (count.compareTo(BigDecimal.ZERO) != 0) {
                balance = total.divide(count, BigDecimal.ROUND_HALF_UP);  
            } else {
                balance = BigDecimal.ZERO;  
            }

            balanceMap.put(member, balance);
        }

        return balanceMap;
    }
}

