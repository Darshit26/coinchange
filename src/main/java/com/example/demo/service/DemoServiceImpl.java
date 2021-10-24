package com.example.demo.service;

import com.example.demo.Repository.DemoRepository;
import com.example.demo.domain.Coin;
import com.example.demo.domain.CoinChangeResponse;
import com.example.demo.domain.CoinRechargeRequest;
import com.example.demo.domain.CoinRechargeResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class DemoServiceImpl implements DemoService {

    private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    @Value("#{'${application.supported-coins}'.split(',')}")
    private List<Double> supportedCoins;

    @Autowired
    private DemoRepository demoRepository;

    public CoinChangeResponse getChange(Integer bill, Boolean getMaximumCoins) throws Exception {
        CoinChangeResponse coinChangeResponse = new CoinChangeResponse();
        coinChangeResponse.setBill(bill);
        List<Coin> coinList = new ArrayList<>();
        if (getMaximumCoins)
            Collections.sort(supportedCoins, Comparator.naturalOrder());
        else
            Collections.sort(supportedCoins, Comparator.reverseOrder());
        Double remainingAmount = (double) bill;
        Coin coin = null;
        //Iterate supported coins
        for (int i = 0; i < supportedCoins.size(); i++) {
            //Determine number of coins needed for a given supported coin to provide change for the entire bill
            Integer numberOfCoinsNeeded = (int) (remainingAmount / supportedCoins.get(i));
            //check the available number of coins for a given supported coin is greater than zero, then coin to retrieve change else continue with next coin.
            if (demoRepository.getAvailableQuantity(supportedCoins.get(i)) > 0) {
                coin = new Coin();
                coin.setCoinValue(supportedCoins.get(i));
                //check if number coins needed is greater than available coins,
                //then set coin quantity as available number of coins
                //else set coin quantity as number of coins needed to provide change for the requested bill.
                if (numberOfCoinsNeeded > demoRepository.getAvailableQuantity(coin.getCoinValue())) {
                    coin.setQuantity(demoRepository.getAvailableQuantity(coin.getCoinValue()));
                } else {
                    coin.setQuantity(numberOfCoinsNeeded);
                }
                //subtract remaining amount with the monetary amount of coins used
                remainingAmount = remainingAmount - (coin.getQuantity() * coin.getCoinValue());
                coinList.add(coin);
                //check if the no amount of the requested bill is still left to be changed, then break for the loop.
                if (Double.compare(remainingAmount, 0.00) == 0) {
                    break;
                }
            } else {
                //if no coins are left, then set message for user.
                if (i == supportedCoins.size() - 1)
                    coinChangeResponse.setErrorMessage("No coin change available. Please try again later.");
                continue;
            }
        }
        //if the coin change is not provided for the entire value of requested bill and error message is not already set, then set message for the user, else set the coins list in the response
        if (remainingAmount > 0 && StringUtils.isBlank(coinChangeResponse.getErrorMessage())) {
            coinChangeResponse.setErrorMessage("There is not enough coin change available for the requested bill. Please try a smaller bill.");
        } else if (coinList.size() > 0) {
            //update the number of available coins left
            coinList.stream().forEach(s -> demoRepository.updateAvailableQuantity(s.getCoinValue(), demoRepository.getAvailableQuantity(s.getCoinValue()) - s.getQuantity()));
            coinChangeResponse.setCoins(coinList);
        }
        return coinChangeResponse;
    }

    @Override
    public CoinRechargeResponse rechargeCoins(CoinRechargeRequest coinRechargeRequest) throws Exception {
        CoinRechargeResponse coinRechargeResponse = new CoinRechargeResponse();
        //update the coin quantity in backend
        coinRechargeRequest.getCoins().stream().forEach(coin -> demoRepository.updateAvailableQuantity(coin.getCoinValue(), (demoRepository.getAvailableQuantity(coin.getCoinValue()) + coin.getQuantity())));
        List<Coin> coinList = new ArrayList<>();
        //retrieve coin quantity from backend
        supportedCoins.stream().forEach(sc -> {
            Coin coin = new Coin();
            coin.setCoinValue(sc);
            coin.setQuantity(demoRepository.getAvailableQuantity(sc));
            coinList.add(coin);
        });
        coinRechargeResponse.setCoins(coinList);
        return coinRechargeResponse;
    }
}
