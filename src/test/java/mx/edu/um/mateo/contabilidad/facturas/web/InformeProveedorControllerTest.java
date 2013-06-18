/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.edu.um.mateo.contabilidad.facturas.web;

import java.util.ArrayList;
import java.util.Date;
import mx.edu.um.mateo.contabilidad.facturas.model.InformeProveedor;
import mx.edu.um.mateo.general.model.Usuario;
import mx.edu.um.mateo.general.test.BaseControllerTest;
import mx.edu.um.mateo.general.test.GenericWebXmlContextLoader;
import mx.edu.um.mateo.general.utils.Constantes;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author develop
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = GenericWebXmlContextLoader.class, locations = {
    "classpath:mateo.xml",
    "classpath:security.xml",
    "classpath:dispatcher-servlet.xml"
})
@Transactional
public class InformeProveedorControllerTest extends BaseControllerTest {

    @Test
    public void testLista() throws Exception {
        log.debug("Debiera mostrar lista de informes de proveedor");

        Usuario usuario = obtieneUsuario();
        InformeProveedor informeProveedor = null;
        for (int i = 0; i < 20; i++) {
            informeProveedor = new InformeProveedor();
            informeProveedor.setEmpresa(usuario.getEmpresa());
            informeProveedor.setFechaInforme(new Date());
            informeProveedor.setNombreProveedor("LAla");
            informeProveedor.setStatus("A");
            currentSession().save(informeProveedor);
            assertNotNull(informeProveedor.getId());
        }
        this.mockMvc.perform(get(Constantes.PATH_INFORMEPROVEEDOR)).
                andExpect(forwardedUrl("/WEB-INF/jsp/" + Constantes.PATH_INFORMEPROVEEDOR_LISTA + ".jsp")).
                andExpect(model().attributeExists(Constantes.CONTAINSKEY_INFORMESPROVEEDOR)).
                andExpect(model().attributeExists(Constantes.CONTAINSKEY_PAGINACION)).
                andExpect(model().attributeExists(Constantes.CONTAINSKEY_PAGINAS)).
                andExpect(model().attributeExists(Constantes.CONTAINSKEY_PAGINA));
    }

    @Test
    public void testNuevo() throws Exception {
        log.debug("Test 'nuevo'");

        this.mockMvc.perform(get(Constantes.PATH_INFORMEPROVEEDOR_NUEVO))
                .andExpect(forwardedUrl("/WEB-INF/jsp/" + Constantes.PATH_INFORMEPROVEEDOR_NUEVO + ".jsp"))
                .andExpect(model().attributeExists(Constantes.ADDATTRIBUTE_INFORMEPROVEEDOR));
    }

    @Test
    public void testEdita() throws Exception {
        log.debug("Test 'edita'");
        Usuario usuario = obtieneUsuario();
        InformeProveedor informeProveedor = new InformeProveedor();
        informeProveedor.setEmpresa(usuario.getEmpresa());
        informeProveedor.setFechaInforme(new Date());
        informeProveedor.setNombreProveedor("LAla");
        informeProveedor.setStatus("A");
        currentSession().save(informeProveedor);
        assertNotNull(informeProveedor.getId());
        this.mockMvc.perform(get(Constantes.PATH_INFORMEPROVEEDOR_EDITA + "/" + informeProveedor.getId()))
                .andExpect(forwardedUrl("/WEB-INF/jsp/" + Constantes.PATH_INFORMEPROVEEDOR_EDITA + ".jsp"))
                .andExpect(model().attributeExists(Constantes.ADDATTRIBUTE_INFORMEPROVEEDOR))
                .andExpect(model().attribute(Constantes.ADDATTRIBUTE_INFORMEPROVEEDOR, informeProveedor));
    }

    @Test
    public void testVer() throws Exception {
        log.debug("Debiera mostrar paquetes");
        Usuario usuario = obtieneUsuario();
        InformeProveedor informeProveedor = new InformeProveedor();
        informeProveedor.setEmpresa(usuario.getEmpresa());
        informeProveedor.setFechaInforme(new Date());
        informeProveedor.setNombreProveedor("LAla");
        informeProveedor.setStatus("A");
        currentSession().save(informeProveedor);
        assertNotNull(informeProveedor.getId());

        this.mockMvc.perform(get(Constantes.PATH_INFORMEPROVEEDOR_VER + "/" + informeProveedor.getId()))
                .andExpect(forwardedUrl("/WEB-INF/jsp/" + Constantes.PATH_INFORMEPROVEEDOR_VER + ".jsp"))
                .andExpect(model().attributeExists(Constantes.ADDATTRIBUTE_INFORMEPROVEEDOR));
    }

    @Test
    public void testGraba() throws Exception {
        log.debug("Debiera crear paquete");
        Usuario usuario = obtieneUsuario();
        this.authenticate(usuario, usuario.getPassword(), new ArrayList<GrantedAuthority>(usuario.getRoles()));


        this.mockMvc.perform(post(Constantes.PATH_INFORMEPROVEEDOR_GRABA)
                .param("nombreProveedor", "0575")
                .param("fechaInforme", "4/06/2013")
                .param("status", "a"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message", "informeProveedor.graba.message"))
                .andExpect(redirectedUrl(Constantes.PATH_PAQUETE_LISTA));
    }

    @Test
    public void testElimina() throws Exception {
        Usuario usuario = obtieneUsuario();
        InformeProveedor informeProveedor = new InformeProveedor();
        informeProveedor.setEmpresa(usuario.getEmpresa());
        informeProveedor.setFechaInforme(new Date());
        informeProveedor.setNombreProveedor("LAla");
        informeProveedor.setStatus("A");
        currentSession().save(informeProveedor);
        assertNotNull(informeProveedor.getId());

        this.mockMvc.perform(post(Constantes.PATH_INFORMEPROVEEDOR_ELIMINA)
                .param("id", informeProveedor.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(flash().attributeExists(Constantes.CONTAINSKEY_MESSAGE))
                .andExpect(flash().attribute(Constantes.CONTAINSKEY_MESSAGE, "informeProveedor.elimina.message"))
                .andExpect(redirectedUrl(Constantes.PATH_INFORMEPROVEEDOR_LISTA));
    }
}
