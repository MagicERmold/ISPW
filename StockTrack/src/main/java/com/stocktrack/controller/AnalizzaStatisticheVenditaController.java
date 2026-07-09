package com.stocktrack.controller;

import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.bean.StatisticaVenditaMensileBean;
import com.stocktrack.exceptions.PersistenceException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AnalizzaStatisticheVenditaController extends GestisciProdottiController {

    private static final int MONTHS_TO_SIMULATE = 6;
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");
    private static final ZoneId APPLICATION_ZONE = ZoneId.systemDefault();

    public List<StatisticaVenditaMensileBean> analizzaStatisticheMensili() throws PersistenceException {
        List<ProdottoBean> prodotti = visualizzaProdotti();
        List<StatisticaVenditaMensileBean> statistiche = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now(APPLICATION_ZONE).minusMonths(MONTHS_TO_SIMULATE - 1L);

        for (int index = 0; index < MONTHS_TO_SIMULATE; index++) {
            YearMonth mese = currentMonth.plusMonths(index);
            statistiche.add(simulaStatisticheMese(mese, prodotti));
        }
        return statistiche;
    }

    private StatisticaVenditaMensileBean simulaStatisticheMese(YearMonth mese, List<ProdottoBean> prodotti) {
        if (prodotti.isEmpty()) {
            return new StatisticaVenditaMensileBean(mese.format(MONTH_FORMATTER), 0, BigDecimal.ZERO, "N/D");
        }

        int quantitaTotale = 0;
        BigDecimal incasso = BigDecimal.ZERO;
        ProdottoBean prodottoPiuVenduto = prodotti.get(0);
        int maxVenduto = -1;

        for (ProdottoBean prodotto : prodotti) {
            int venduto = simulaVenduto(prodotto, mese);
            quantitaTotale += venduto;
            BigDecimal prezzo = prodotto.getPrezzoUnitario() == null ? BigDecimal.ZERO : prodotto.getPrezzoUnitario();
            incasso = incasso.add(prezzo.multiply(BigDecimal.valueOf(venduto)));
            if (venduto > maxVenduto) {
                maxVenduto = venduto;
                prodottoPiuVenduto = prodotto;
            }
        }

        return new StatisticaVenditaMensileBean(mese.format(MONTH_FORMATTER), quantitaTotale,
                incasso.setScale(2, RoundingMode.HALF_UP), prodottoPiuVenduto.getNome());
    }

    private int simulaVenduto(ProdottoBean prodotto, YearMonth mese) {
        int base = Math.abs((prodotto.getId() + mese).hashCode() % 9) + 1;
        int limiteSensato = Math.max(1, prodotto.getQuantita() + base);
        return Math.min(limiteSensato, base + mese.getMonthValue() % 4);
    }
}
