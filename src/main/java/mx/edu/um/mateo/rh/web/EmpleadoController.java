/*
 * The MIT License
 *
 * Copyright 2012 Universidad de Montemorelos A. C.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package mx.edu.um.mateo.rh.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import mx.edu.um.mateo.general.dao.EmpresaDao;
import mx.edu.um.mateo.general.model.Empresa;
import mx.edu.um.mateo.general.model.Usuario;
import mx.edu.um.mateo.general.utils.Constantes;
import mx.edu.um.mateo.general.utils.ObjectRetrievalFailureException;
import mx.edu.um.mateo.general.utils.ReporteException;
import mx.edu.um.mateo.general.web.BaseController;
import mx.edu.um.mateo.inventario.dao.AlmacenDao;
import mx.edu.um.mateo.inventario.model.Almacen;
import mx.edu.um.mateo.rh.model.ClaveEmpleado;
import mx.edu.um.mateo.rh.model.Empleado;
import mx.edu.um.mateo.rh.model.NivelEstudios;
import mx.edu.um.mateo.rh.model.TipoEmpleado;
import mx.edu.um.mateo.rh.service.ClaveEmpleadoManager;
import mx.edu.um.mateo.rh.service.EmpleadoManager;
import mx.edu.um.mateo.rh.service.TipoEmpleadoManager;
import mx.edu.um.mateo.rh.service.VacacionesEmpleadoManager;
import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author osoto@um.edu.mx
 */
@Controller
@RequestMapping("/rh/empleado")
public class EmpleadoController extends BaseController {

    @Autowired
    private EmpleadoManager empleadoManager;
    @Autowired
    private VacacionesEmpleadoManager vacacionesManager;
    @Autowired
    private ClaveEmpleadoManager claveManager;
    @Autowired
    private TipoEmpleadoManager tipoEmpleadoManager;
    @Autowired
    private AlmacenDao almacenDao;
    @Autowired
    private EmpresaDao empresaDao;

    public EmpleadoController() {
        log.info("Se ha creado una nueva instancia de EmpleadoController");
    }

    @RequestMapping
    public String lista(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) Long pagina,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String sort,
            Model modelo) {
        log.debug("Mostrando lista de empleados");
        Map<String, Object> params = new HashMap<>();
        Long empresaId = (Long) request.getSession().getAttribute("empresaId");
        params.put("empresa", empresaId);
        if (StringUtils.isNotBlank(filtro)) {
            params.put("filtro", filtro);
        }
        if (StringUtils.isNotBlank(order)) {
            params.put("order", order);
            params.put("sort", sort);
        }
        if (pagina != null) {
            params.put("pagina", pagina);
        }

        if (StringUtils.isNotBlank(tipo)) {
            params.put("reporte", true);
            params = empleadoManager.lista(params);
            try {
                generaReporte(tipo, (List<Empleado>) params.get(Constantes.EMPLEADO_LIST), response, "empleados", Constantes.EMP, empresaId);
                return null;
            } catch (ReporteException e) {
                log.error("No se pudo generar el reporte", e);
            }
        }

        if (StringUtils.isNotBlank(correo)) {
            params.put("reporte", true);
            params = empleadoManager.lista(params);

            params.remove("reporte");
            try {
                enviaCorreo(correo, (List<Empleado>) params.get(Constantes.EMPLEADO_LIST), request, "empleados", Constantes.EMP, empresaId);
                modelo.addAttribute("message", "lista.enviada.message");
                modelo.addAttribute("messageAttrs", new String[]{messageSource.getMessage("empleado.lista.label", null, request.getLocale()), ambiente.obtieneUsuario().getUsername()});
            } catch (ReporteException e) {
                log.error("No se pudo enviar el reporte por correo", e);
            }
        }
        params = empleadoManager.lista(params);
        modelo.addAttribute(Constantes.EMPLEADO_LIST, params.get(Constantes.EMPLEADO_LIST));

        this.pagina(params, modelo, Constantes.EMPLEADO_LIST, pagina);

        return Constantes.PATH_EMPLEADO_LISTA;
    }

    @RequestMapping("/ver/{id}")
    public String ver(HttpServletRequest request, @PathVariable Long id, Model modelo) throws ObjectRetrievalFailureException {
        log.debug("Mostrando empleado {}", id);
        int diasLibres = vacacionesManager.totalDias();
        Empleado empleado = empleadoManager.obtiene(id);
        ClaveEmpleado clave = claveManager.obtieneClaveActiva(id);
        empleado.setClaveActual(clave.getClave());

        modelo.addAttribute("diasLibres", diasLibres);
        modelo.addAttribute(Constantes.EMPLEADO_KEY, empleado);

        request.getSession().setAttribute(Constantes.EMPLEADO_KEY, empleado);

        return Constantes.PATH_EMPLEADO_VER;
    }

    @RequestMapping("/nuevo")
    public String nuevo(Model modelo) {
        log.debug("Nuevo empleado");
        Map<String, Object> params = new HashMap<>();
        params = tipoEmpleadoManager.lista(params);
        modelo.addAttribute(Constantes.TIPOEMPLEADO_LIST, (List) params.get(Constantes.TIPOEMPLEADO_LIST));
        Empleado empleado = new Empleado();

        modelo.addAttribute(Constantes.EMPLEADO_KEY, empleado);
        modelo.addAttribute(Constantes.NIVELESTUDIOS_LIST, NivelEstudios.values());

        return Constantes.PATH_EMPLEADO_NUEVO;
    }

    @RequestMapping(value = "/graba", method = RequestMethod.POST)
    public String graba(HttpServletRequest request, HttpServletResponse response, @Valid Empleado empleado, BindingResult bindingResult, Errors errors, Model modelo, RedirectAttributes redirectAttributes) {
        for (String nombre : request.getParameterMap().keySet()) {
            log.debug("Param: {} : {}", nombre, request.getParameterMap().get(nombre));
        }
        if (bindingResult.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.debug("Error: {}", error);
            }
            return Constantes.PATH_EMPLEADO_NUEVO;
        }
        String t = request.getParameter("tipoEmpleado.id");
        TipoEmpleado tipoEmpleado = tipoEmpleadoManager.obtiene(Long.valueOf(t));
        empleado.setTipoEmpleado(tipoEmpleado);
        Usuario usuario = ambiente.obtieneUsuario();
        String password = null;
        password = KeyGenerators.string().generateKey();
        empleado.setPassword(password);
        empleado.setAlmacen(usuario.getAlmacen());
        String filtro = tipoEmpleado.getPrefijo();
        ClaveEmpleado clave = claveManager.nuevaClave(usuario, filtro);
        try {
            empleadoManager.saveEmpleado(empleado, usuario, clave);

            ambiente.actualizaSesion(request.getSession(), usuario);
        } catch (ConstraintViolationException e) {
            log.error("No se pudo crear al empleado", e);
            errors.rejectValue("nombre", "empleado.errors.creado", e.toString());
            return Constantes.PATH_EMPLEADO_NUEVO;
        }

        redirectAttributes.addFlashAttribute("message", "empleado.creado.message");
        redirectAttributes.addFlashAttribute("messageAttrs", new String[]{empleado.getNombre()});

        return "redirect:" + Constantes.PATH_EMPLEADO;
    }

    @RequestMapping("/edita/{id}")
    public String edita(@PathVariable Long id, HttpServletRequest request, @Valid Empleado empleado, BindingResult bindingResult,
            Errors errors, Model modelo, RedirectAttributes redirectAttributes) {
        log.debug("Edita empleado {}", id);
        try {
            empleado = empleadoManager.obtiene(id);
        } catch (ObjectRetrievalFailureException ex) {
            log.error("No se pudo obtener al empleado", ex);
            errors.rejectValue("empleado", "registro.noEncontrado", new String[]{"empleado"}, null);
            return Constantes.PATH_EMPLEADO;
        }
        modelo.addAttribute(Constantes.EMPLEADO_KEY, empleado);
        return Constantes.PATH_EMPLEADO_EDITA;
    }

    @RequestMapping(value = "/actualiza", method = RequestMethod.POST)
    public String actualiza(HttpServletRequest request, @Valid Empleado empleado,
            BindingResult bindingResult, Errors errors, Model modelo,
            RedirectAttributes redirectAttributes,
            @RequestParam(required = false) String[] centrosDeCostoIds) throws ObjectRetrievalFailureException {

        if (bindingResult.hasErrors()) {
            log.error("Hubo algun error en la forma, regresando");
            return Constantes.PATH_EMPLEADO_EDITA;
        }
        Empleado empleadoTmp = empleadoManager.getEmpleado(empleado);
        empleadoTmp.setAdventista(empleado.getAdventista());
        empleadoTmp.setApMaterno(empleado.getApMaterno());
        empleadoTmp.setApPaterno(empleado.getApPaterno());
        empleadoTmp.setConyuge(empleado.getConyuge());
        empleadoTmp.setCuenta(empleado.getCuenta());
        empleadoTmp.setCurp(empleado.getCurp());
        empleadoTmp.setDireccion(empleado.getDireccion());
        empleadoTmp.setEscalafon(empleado.getEscalafon());
        empleadoTmp.setEstadoCivil(empleado.getEstadoCivil());
        empleadoTmp.setExperienciaFueraUm(empleado.getExperienciaFueraUm());
        empleadoTmp.setFechaNacimiento(empleado.getFechaNacimiento());
        empleadoTmp.setFechaMatrimonio(empleado.getFechaMatrimonio());
        empleadoTmp.setFinadoMadre(empleado.getFinadoMadre());
        empleadoTmp.setFinadoPadre(empleado.getFinadoPadre());
        empleadoTmp.setGenero(empleado.getGenero());
        empleadoTmp.setIfe(empleado.getIfe());
        empleadoTmp.setIglesia(empleado.getIglesia());
        empleadoTmp.setImms(empleado.getImms());
        empleadoTmp.setMadre(empleado.getMadre());
        empleadoTmp.setModalidad(empleado.getModalidad());
        empleadoTmp.setPadre(empleado.getPadre());
        empleadoTmp.setRango(empleado.getRango());
        empleadoTmp.setResponsabilidad(empleado.getResponsabilidad());
        empleadoTmp.setRfc(empleado.getRfc());
        empleadoTmp.setStatus(empleado.getStatus());
        empleadoTmp.setTurno(empleado.getTurno());
        empleadoTmp.setNombre(empleado.getNombre());
        try {
            empleadoManager.actualizaEmpleado(empleadoTmp);
        } catch (ConstraintViolationException e) {
            log.error("No se pudo crear al empleado", e);
            errors.rejectValue("nombre", "empleado.errors.creado", e.toString());
            return Constantes.PATH_EMPLEADO_EDITA;
        }

        redirectAttributes.addFlashAttribute("message",
                "usuario.actualizado.message");
        redirectAttributes.addFlashAttribute("messageAttrs",
                new String[]{empleado.getUsername()});

        return "redirect:" + Constantes.PATH_EMPLEADO;
    }

    @RequestMapping(value = "/elimina", method = RequestMethod.POST)
    public String elimina(HttpServletRequest request, @RequestParam Long id, Model modelo, @ModelAttribute Empleado empleado, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        log.debug("Elimina empleado");
        try {
            Long empresaId = (Long) request.getSession().getAttribute("empresaId");
            String nombre = empleadoManager.elimina(id);

            ambiente.actualizaSesion(request.getSession());

            redirectAttributes.addFlashAttribute("message", "empleado.eliminado.message");
            redirectAttributes.addFlashAttribute("messageAttrs", new String[]{nombre});
        } catch (Exception e) {
            log.error("No se pudo eliminar el empleado " + id, e);
            bindingResult.addError(new ObjectError("empleado", new String[]{"empleado.no.eliminado.message"}, null, null));
            return Constantes.PATH_EMPLEADO_VER;
        }
        return "redirect:" + Constantes.PATH_EMPLEADO;
    }

    @RequestMapping("/datos/{id}")
    public String datos(@PathVariable Long id, Model modelo) throws ObjectRetrievalFailureException {
        log.debug("Mostrando empleado {}", id);

        Empleado empleado = empleadoManager.obtiene(id);

        modelo.addAttribute(Constantes.EMPLEADO_KEY, empleado);

        return "/rh/empleado/datos";
    }
}
