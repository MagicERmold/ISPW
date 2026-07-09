package com.stocktrack.pattern.factory;

import com.stocktrack.persistence.dao.CommessoDAO;
import com.stocktrack.persistence.dao.FornitoreDAO;
import com.stocktrack.persistence.dao.InventarioDAO;
import com.stocktrack.persistence.dao.OrdineDAO;
import com.stocktrack.persistence.dao.ProdottoDAO;
import com.stocktrack.persistence.dao.TitolareDAO;
import com.stocktrack.persistence.fs.FileSystemCommessoDAO;
import com.stocktrack.persistence.fs.FileSystemFornitoreDAO;
import com.stocktrack.persistence.fs.FileSystemInventarioDAO;
import com.stocktrack.persistence.fs.FileSystemOrdineDAO;
import com.stocktrack.persistence.fs.FileSystemProdottoDAO;
import com.stocktrack.persistence.fs.FileSystemTitolareDAO;

public class FileSystemDAOFactory extends DAOFactory {

    @Override
    public TitolareDAO getTitolareDAO() {
        return new FileSystemTitolareDAO();
    }

    @Override
    public CommessoDAO getCommessoDAO() {
        return new FileSystemCommessoDAO();
    }

    @Override
    public FornitoreDAO getFornitoreDAO() {
        return new FileSystemFornitoreDAO();
    }

    @Override
    public ProdottoDAO getProdottoDAO() {
        return new FileSystemProdottoDAO();
    }

    @Override
    public InventarioDAO getInventarioDAO() {
        return new FileSystemInventarioDAO();
    }

    @Override
    public OrdineDAO getOrdineDAO() {
        return new FileSystemOrdineDAO();
    }
}
