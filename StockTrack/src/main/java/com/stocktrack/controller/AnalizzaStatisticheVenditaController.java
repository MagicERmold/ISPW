package com.stocktrack.controller;

import com.stocktrack.bean.ProdottoBean;
import com.stocktrack.bean.StatisticaVenditaMensileBean;
import com.stocktrack.entity.MovimentoInventario;
import com.stocktrack.entity.Prodotto;
import com.stocktrack.entity.TipoMovimentoInventario;
import com.stocktrack.exceptions.PersistenceException;
import com.stocktrack.pattern.factory.DAOFactoryProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnalizzaStatisticheVenditaController {

    private static final int MONTHS_TO_SIMULATE = 6;
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");
    private static final ZoneId APPLICATION_ZONE = ZoneId.systemDefault();

    public List<StatisticaVenditaMensileBean> analizzaStatisticheMensili() throws PersistenceException {
        List<ProdottoBean> prodotti = visualizzaProdotti();
        Map<YearMonth, StatisticaVenditaMensileBean> statistiche = new LinkedHashMap<>();
        YearMonth currentMonth = YearMonth.now(APPLICATION_ZONE).minusMonths(MONTHS_TO_SIMULATE - 1L);

        for (int index = 0; index < MONTHS_TO_SIMULATE; index++) {
            YearMonth mese = currentMonth.plusMonths(index);
            statistiche.put(mese, simulaStatisticheMese(mese, prodotti));
        }
        integraMovimentiReali(statistiche);
        return new ArrayList<>(statistiche.values());
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

    private void integraMovimentiReali(Map<YearMonth, StatisticaVenditaMensileBean> statistiche)
            throws PersistenceException {
        for (MovimentoInventario movimento : DAOFactoryProvider.getFactory().getMovimentoInventarioDAO().findAll()) {
            if (movimento.getDataMovimento() != null && movimento.getTipo() != null) {
                integraMovimentoReale(statistiche, movimento);
            }
        }
    }

    private void integraMovimentoReale(Map<YearMonth, StatisticaVenditaMensileBean> statistiche,
                                       MovimentoInventario movimento) {
        YearMonth mese = YearMonth.from(movimento.getDataMovimento());
        StatisticaVenditaMensileBean statistica = statistiche.computeIfAbsent(mese,
                key -> new StatisticaVenditaMensileBean(key.format(MONTH_FORMATTER), 0, BigDecimal.ZERO, "N/D"));
        BigDecimal valoreMovimento = movimento.getValoreUnitario() == null ? BigDecimal.ZERO
                : movimento.getValoreUnitario().multiply(BigDecimal.valueOf(movimento.getQuantita()));

        if (TipoMovimentoInventario.VENDITA.equals(movimento.getTipo())) {
            statistica.setQuantitaVenduta(statistica.getQuantitaVenduta() + movimento.getQuantita());
            statistica.setIncassoStimato(statistica.getIncassoStimato().add(valoreMovimento)
                    .setScale(2, RoundingMode.HALF_UP));
            statistica.setProdottoPiuVenduto(movimento.getNomeProdotto());
        } else {
            statistica.setQuantitaAcquistata(statistica.getQuantitaAcquistata() + movimento.getQuantita());
            statistica.setSpesaAcquisti(statistica.getSpesaAcquisti().add(valoreMovimento)
                    .setScale(2, RoundingMode.HALF_UP));
        }
    }

    private int simulaVenduto(ProdottoBean prodotto, YearMonth mese) {
        int base = Math.abs((prodotto.getId() + mese).hashCode() % 9) + 1;
        int limiteSensato = Math.max(1, prodotto.getQuantita() + base);
        return Math.min(limiteSensato, base + mese.getMonthValue() % 4);
    }

    private List<ProdottoBean> visualizzaProdotti() throws PersistenceException {
        return DAOFactoryProvider.getFactory().getProdottoDAO().findAll().stream()
                .map(this::toProdottoBean)
                .toList();
    }

    private ProdottoBean toProdottoBean(Prodotto prodotto) {
        return new ProdottoBean(prodotto.getId(), prodotto.getNome(), prodotto.getCategoria(),
                prodotto.getQuantita(), prodotto.getSogliaMinima(), prodotto.getPrezzoUnitario());
    }
}
