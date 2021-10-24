package com.example.demo.controller;

import com.example.demo.domain.CoinChangeResponse;
import com.example.demo.domain.CoinRechargeRequest;
import com.example.demo.domain.CoinRechargeResponse;
import com.example.demo.domain.Error;
import com.example.demo.service.DemoService;
import com.example.demo.utility.Validator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    private DemoService demoService;

    @Autowired
    private Validator validator;

    @GetMapping(value = "/bills/{bill}/coinChange", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCoinChange(@PathVariable(value = "bill") String bill, @RequestParam(value = "getMaximumCoins", required = false) String getMaximumCoins) throws Exception {
        CoinChangeResponse coinChangeResponse = null;
        Error error = null;
        try {
            error = validator.validateGetCoinChangeRequest(bill, getMaximumCoins);
            if (null != error) {
                return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
            }
            coinChangeResponse = demoService.getChange(Integer.parseInt(bill), Boolean.parseBoolean(getMaximumCoins));
            if (StringUtils.isNotBlank(coinChangeResponse.getErrorMessage())) {
                return new ResponseEntity<CoinChangeResponse>(coinChangeResponse, HttpStatus.FAILED_DEPENDENCY);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            List<String> errorList = new ArrayList<>();
            errorList.add("Error in application. Please try again later.");
            error = new Error();
            error.setErrors(errorList);
            return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<CoinChangeResponse>(coinChangeResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/coins/recharges", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> rechargeCoins(@RequestBody CoinRechargeRequest coinRechargeRequest) throws Exception {
        CoinRechargeResponse coinRechargeResponse = null;
        Error error = null;
        try {
            error = validator.validateCoinRechargeRequest(coinRechargeRequest);
            if (null != error) {
                return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
            }
            coinRechargeResponse = demoService.rechargeCoins(coinRechargeRequest);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw ex;
        }
        return new ResponseEntity<CoinRechargeResponse>(coinRechargeResponse, HttpStatus.OK);
    }

}
