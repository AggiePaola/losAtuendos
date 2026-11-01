package com.mycompany.losatuendos.facade;

import com.mycompany.losatuendos.builder.ServicioAlquilerBuilder;
import com.mycompany.losatuendos.composite.ComponenteAlquiler;
import com.mycompany.losatuendos.composite.ConjuntoPrendas;
import com.mycompany.losatuendos.composite.PrendaIndividual;
import com.mycompany.losatuendos.decorator.PrioridadDecorator;
import com.mycompany.losatuendos.decorator.PrioridadLavanderia;
import com.mycompany.losatuendos.decorator.PrioridadPrenda;
import com.mycompany.losatuendos.decorator.Priorizable;
import com.mycompany.losatuendos.factory.PrendaFactory;
import com.mycompany.losatuendos.modelo.*;
import com.mycompany.losatuendos.persistencia.*;

import java.sql.SQLException;
import java.util.List;

public class FacadeAlquiler {

    private final RepositorioClientes repoClientes = new RepositorioClientes();
    private final RepositorioEmpleados repoEmpleados = new RepositorioEmpleados();
    private final RepositorioPrendasH2 repoPrendas = new RepositorioPrendasH2();
    private final RepositorioServicios repoServicios = new RepositorioServicios();
    private final RepositorioLavanderia repoLavanderia = new RepositorioLavanderia();
    private final PrendaFactory factory = new PrendaFactory();

    public void registrarCliente(Cliente c) throws SQLException {
        if (c.getDocumento() == null || c.getDocumento().isBlank())
            throw new IllegalArgumentException("El documento del cliente es obligatorio.");
        if (c.getNombres() == null || c.getNombres().isBlank())
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        if (c.getApellidos() == null || c.getApellidos().isBlank())
            throw new IllegalArgumentException("Los apellidos del cliente son obligatorios.");
        if (repoClientes.existeDocumento(c.getDocumento()))
            throw new IllegalArgumentException("Ya existe un cliente con el documento " + c.getDocumento());
        repoClientes.guardar(c);
    }
    public List<Cliente> listarClientes() throws SQLException {
        return repoClientes.listarTodos();
    }
    public Cliente buscarCliente(int id) throws SQLException {
        return repoClientes.buscarPorId(id);
    }
    public void registrarEmpleado(Empleado e) throws SQLException {
        if (e.getDocumento() == null || e.getDocumento().isBlank())
            throw new IllegalArgumentException("El documento del empleado es obligatorio.");
        if (e.getNombres() == null || e.getNombres().isBlank())
            throw new IllegalArgumentException("El nombre del empleado es obligatorio.");
        if (e.getApellidos() == null || e.getApellidos().isBlank())
            throw new IllegalArgumentException("Los apellidos del empleado son obligatorios.");
        if (repoEmpleados.existeDocumento(e.getDocumento()))
            throw new IllegalArgumentException("Ya existe un empleado con el documento " + e.getDocumento());
        repoEmpleados.guardar(e);
    }
    public List<Empleado> listarEmpleados() throws SQLException {
        return repoEmpleados.listarTodos();
    }
    public Empleado buscarEmpleado(int id) throws SQLException {
        return repoEmpleados.buscarPorId(id);
    }

    public Prenda registrarPrenda(TipoPrenda tipo, String codigo, String nombre,
                                  String descripcion, String atributoExtra,
                                  boolean atributoBool) throws SQLException {
        if (codigo == null || codigo.isBlank())
            throw new IllegalArgumentException("El codigo de la prenda es obligatorio.");
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre de la prenda es obligatorio.");
        if (repoPrendas.buscarPorCodigo(codigo) != null)
            throw new IllegalArgumentException("Ya existe una prenda con el codigo " + codigo);

        Prenda p = factory.crearPrenda(tipo, codigo, nombre, descripcion,
                                       atributoExtra, atributoBool);
        repoPrendas.guardar(p);
        return p;
    }
    public List<Prenda> listarPrendas() throws SQLException {
        return repoPrendas.listarTodas();
    }
    public Prenda buscarPrenda(int id) throws SQLException {
        return repoPrendas.buscarPorId(id);
    }
    public boolean prendaDisponiblePara(int idPrenda, String fecha) throws SQLException {
        return repoPrendas.disponiblePara(idPrenda, fecha);
    }

    public ServicioAlquiler crearServicioAlquiler(int idCliente, int idEmpleado,
                                                  List<Integer> idsPrendas,
                                                  String fechaAlquiler,
                                                  String fechaDevolucion,
                                                  double precioTotal)
            throws SQLException {

        Cliente cliente = repoClientes.buscarPorId(idCliente);
        if (cliente == null)
            throw new IllegalArgumentException("El cliente con id " + idCliente + " no existe.");
        Empleado empleado = repoEmpleados.buscarPorId(idEmpleado);
        if (empleado == null)
            throw new IllegalArgumentException("El empleado con id " + idEmpleado + " no existe.");
        if (idsPrendas == null || idsPrendas.isEmpty())
            throw new IllegalArgumentException("Debe incluir al menos una prenda.");
        ServicioAlquilerBuilder builder = new ServicioAlquilerBuilder()
                .conCliente(cliente)
                .conEmpleado(empleado)
                .conFechaAlquiler(fechaAlquiler)
                .conFechaDevolucion(fechaDevolucion)
                .conPrecioTotal(precioTotal);
        for (int idP : idsPrendas) {
            Prenda p = repoPrendas.buscarPorId(idP);
            if (p == null)
                throw new IllegalArgumentException("La prenda con id " + idP + " no existe.");
            if (!repoPrendas.disponiblePara(idP, fechaAlquiler))
                throw new IllegalArgumentException(
                        "La prenda " + p.getCodigo() + " no esta disponible para la fecha " + fechaAlquiler);
            builder.agregarPrenda(p);
        }
        if (fechaAlquiler == null || fechaAlquiler.isBlank())
            throw new IllegalArgumentException("La fecha de alquiler es obligatoria.");
        if (fechaDevolucion == null || fechaDevolucion.isBlank())
            throw new IllegalArgumentException("La fecha de devolucion es obligatoria.");

        int consecutivo = repoServicios.siguienteConsecutivo();
        ServicioAlquiler servicio = builder.construir(consecutivo);
        repoServicios.guardar(servicio);

        for (Prenda p : servicio.getListaPrendas()) {
            marcarNoDisponible(p.getId());
        }
        return servicio;
    }
    private void marcarNoDisponible(int idPrenda) throws SQLException {
        try (java.sql.Connection conn = ConexionBD.obtener();
             java.sql.PreparedStatement ps = conn.prepareStatement(
                     "UPDATE prendas SET disponible = FALSE WHERE id = ?")) {
            ps.setInt(1, idPrenda);
            ps.executeUpdate();
        }
    }
    public List<ServicioAlquiler> listarServicios() throws SQLException {
        return repoServicios.listarTodos();
    }
    public List<ServicioAlquiler> listarVigentes() throws SQLException {
        return repoServicios.listarVigentes();
    }
    public List<ServicioAlquiler> alquileresVigentesPorCliente(int idCliente) throws SQLException {
        return repoServicios.alquileresVigentesPorCliente(idCliente);
    }

    public void devolverServicio(int idServicio, String fecha,
                                 List<Integer> idsPrioritarias) throws SQLException {
        ServicioAlquiler servicio = repoServicios.buscarPorId(idServicio);
        if (servicio == null)
            throw new IllegalArgumentException("El servicio " + idServicio + " no existe.");
        if (servicio.getEstado() == EstadoAlquiler.DEVUELTO)
            throw new IllegalArgumentException("El servicio ya fue devuelto.");
        repoServicios.cambiarEstado(idServicio, EstadoAlquiler.DEVUELTO);
        for (Prenda p : servicio.getListaPrendas()) {
            boolean prioritaria = idsPrioritarias != null && idsPrioritarias.contains(p.getId());
            PrioridadLavanderia prio = prioritaria
                    ? PrioridadLavanderia.PRIORITARIA
                    : PrioridadLavanderia.NORMAL;
            repoLavanderia.registrarIngreso(p.getId(), fecha, prio);
        }
    }

    public List<Priorizable> listarPendientesLavanderia() throws SQLException {
        List<PrioridadPrenda> base = repoLavanderia.listarPendientes();
        List<Priorizable> resultado = new java.util.ArrayList<>();
        for (PrioridadPrenda pp : base) {
            if (pp.getPrioridad() == PrioridadLavanderia.PRIORITARIA) {
                // Aplicar decorator
                resultado.add(new PrioridadDecorator(pp));
            } else {
                resultado.add(pp);
            }
        }
        resultado.sort((a, b) -> {
            int cmp = Integer.compare(b.getNivelPrioridad(), a.getNivelPrioridad());
            return cmp;
        });
        return resultado;
    }
    public List<TandaLavanderia> organizarTandas(int capacidad) throws SQLException {
        return repoLavanderia.organizarTandas(capacidad);
    }
    public void marcarPrendaLavada(int idPrenda) throws SQLException {
        repoLavanderia.marcarLavada(idPrenda);
    }
    public List<TandaLavanderia> historialTandas() throws SQLException {
        return repoLavanderia.historialTandas();
    }
}
