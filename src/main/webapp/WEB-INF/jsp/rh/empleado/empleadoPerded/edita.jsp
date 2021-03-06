<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
   <head>
        <title><s:message code="empleadoPerDed.edita.label" /></title>
    </head>
    <body>
        <jsp:include page="../menu.jsp" >
            <jsp:param name="menu" value="empleado" />
        </jsp:include>

        <div id="nuevo-empleadoPerDed" class="content scaffold-list" role="main">
            <h1><s:message code="empleadoPerDed.edita.label" /></h1>
            <h3><c:out value="${empleado.clave}" />&nbsp;<c:out value="${empleado.nombreCompleto}" /></h3>
            <p class="well">
                <a class="btn btn-primary" href="<s:url value='/rh/empleado/empleadoPerded/'/>"><i class="icon-list icon-white"></i> <s:message code='empleadoPerDed.lista.label' /></a>
            </p>
            <c:url var="actualizaUrl" value="/rh/empleado/empleadoPerded/graba" />
            <form:form commandName="empleadoPerDed" action="${actualizaUrl}" method="post">
                <form:hidden path="perDed.id" id="perDedId" />
                <form:hidden path="id" id="id" />
                <form:hidden path="version" id="version" />
                
                <form:errors path="*">
                    <div class="alert alert-block alert-error fade in" role="status">
                        <a class="close" data-dismiss="alert">×</a>
                        <c:forEach items="${messages}" var="message">
                            <p>${message}</p>
                        </c:forEach>
                    </div>
                </form:errors>
                <fieldset>
                    <s:bind path="empleadoPerDed.perDed.clave">
                        <div class="control-group <c:if test='${not empty status.errorMessages}'>error</c:if>">
                            <label for="perDed.clave">
                                <s:message code="empleadoPerDed.clave.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <input id="clave" name="clave" class="input-large search-query" value="${perDedForm}">    
                            <form:errors path="perDed.clave" cssClass="alert alert-error" />
                        </div>
                    </s:bind>
                    <s:bind path="empleadoPerDed.importe">
                        <div class="control-group <c:if test='${not empty status.errorMessages}'>error</c:if>">
                            <label for="importe">
                                <s:message code="empleadoPerDed.importe.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <form:input path="importe" cssClass="span08" />
                            <form:errors path="importe" cssClass="alert alert-error" />
                        </div>
                    </s:bind>
                    <s:bind path="empleadoPerDed.porcentaje">
                        <div class="control-group <c:if test='${not empty status.errorMessages}'>error</c:if>">
                            <label for="porcentaje">
                                <s:message code="empleadoPerDed.porcentaje.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <form:radiobutton path="porcentaje"  value="true" cssClass="span3" id="pct"/>Porcentaje<br />
                            <form:radiobutton path="porcentaje"  value="false"  cssClass="span3" id="cash"/>$Importe<br />
                            <form:errors path="porcentaje" cssClass="alert alert-error" />
                        </div>
                    </s:bind>
                    <s:bind path="empleadoPerDed.frecuenciaPago">
                        <div class="control-group <c:if test='${not empty status.errorMessages}'>error</c:if>">
                            <label for="frecuenciaPago">
                                <s:message code="empleadoPerDed.frecuenciaPago.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <form:select id="frecuenciaPago" path="frecuenciaPago" required="true" cssClass="span08" >
                                <form:options items="${frecuenciaPagoList}" itemValue="id" itemLabel="nombre" />
                            </form:select>
                            <form:errors path="frecuenciaPago" cssClass="alert alert-error" />
                        </div>
                    </s:bind>
                    <s:bind path="empleadoPerDed.atributos">
                        <div class="control-group <c:if test='${not empty status.errorMessages}'>error</c:if>">
                            <label for="atributos">
                                <s:message code="empleadoPerDed.atributos.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <form:input path="atributos" cssClass="span08" />
                            <form:errors path="atributos" cssClass="alert alert-error" />
                        </div>
                    </s:bind>
                    
                </fieldset>
                <p class="well" style="margin-top: 10px;">
                    <button type="submit" name="actualiza" class="btn btn-primary btn-large" id="crear" ><i class="icon-ok icon-white"></i>&nbsp;<s:message code='actualizar.button'/></button>
                    <a class="btn btn-large" href="<s:url value='/rh/empleado/empleadoPerded'/>"><i class="icon-remove"></i> <s:message code='cancelar.button' /></a>
                </p>
            </form:form>
        </div>
        <content>
            <script src="<c:url value='/js/lista.js' />"></script>
            <script>
                $(document).ready(function() {
                    $('input#nombre').focus();
                });
                $(function() {
                    $( "#clave" ).autocomplete({
                        source: '${pageContext. request. contextPath}/nomina/catalogos/perded/get_perDed_list',
                        select: function(event, ui) {
                                $("input#clave").val(ui.item.value);
                                $("input#perDedId").val(ui.item.id);
                                return false;
                            }
                    })
                });
          </script>
    </content>
    </body>
</html>
