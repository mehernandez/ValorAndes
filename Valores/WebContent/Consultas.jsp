<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%> 
    <!--JSTL core tags-->
<%@ page import="java.sql.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<!-- This is a directive space -->
<%@ page import="java.util.*" %>
<%@ page session="true" %>
<% String login = (String)(request.getSession().getAttribute("login")) ;
String tipo = (String)(request.getSession().getAttribute("tipo"));%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<%ArrayList tiposValor = (ArrayList)(request.getSession().getAttribute("tiposValor"));
 ArrayList tiposRentabilidad = (ArrayList)(request.getSession().getAttribute("tiposRentabilidad"));%>

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

<title>Valores de los Andes</title>

    <!-- Bootstrap Core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="css/sb-admin.css" rel="stylesheet">

    <!-- Morris Charts CSS -->
    <link href="css/plugins/morris.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="font-awesome-4.1.0/css/font-awesome.min.css" rel="stylesheet" type="text/css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body>

    <div id="wrapper">

        <!-- Navigation -->
        <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="index.html">Valores de los Andes</a>
            </div>
            <!-- Top Menu Items -->
            <ul class="nav navbar-right top-nav">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-envelope"></i> <b class="caret"></b></a>
                    <ul class="dropdown-menu message-dropdown">
                        <li class="message-preview">
                            <a href="#">
                                <div class="media">
                                    <span class="pull-left">
                                        <img class="media-object" src="http://placehold.it/50x50" alt="">
                                    </span>
                                    <div class="media-body">
                                        <h5 class="media-heading"><strong>Login</strong>
                                        </h5>
                                        <p class="small text-muted"><i class="fa fa-clock-o"></i> Yesterday at 4:32 PM</p>
                                        <p>Lorem ipsum dolor sit amet, consectetur...</p>
                                    </div>
                                </div>
                            </a>
                        </li>
                        <li class="message-preview">
                            <a href="#">
                                <div class="media">
                                    <span class="pull-left">
                                        <img class="media-object" src="http://placehold.it/50x50" alt="">
                                    </span>
                                    <div class="media-body">
                                        <h5 class="media-heading"><strong>John Smith</strong>
                                        </h5>
                                        <p class="small text-muted"><i class="fa fa-clock-o"></i> Yesterday at 4:32 PM</p>
                                        <p>Lorem ipsum dolor sit amet, consectetur...</p>
                                    </div>
                                </div>
                            </a>
                        </li>
                        <li class="message-preview">
                            <a href="#">
                                <div class="media">
                                    <span class="pull-left">
                                        <img class="media-object" src="http://placehold.it/50x50" alt="">
                                    </span>
                                    <div class="media-body">
                                        <h5 class="media-heading"><strong><%=login %></strong>
                                        </h5>
                                        <p class="small text-muted"><i class="fa fa-clock-o"></i> Yesterday at 4:32 PM</p>
                                        <p>Lorem ipsum dolor sit amet, consectetur...</p>
                                    </div>
                                </div>
                            </a>
                        </li>
                        <li class="message-footer">
                            <a href="#">Read All New Messages</a>
                        </li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-bell"></i> <b class="caret"></b></a>
                    <ul class="dropdown-menu alert-dropdown">
                        <li>
                            <a href="#">Alert Name <span class="label label-default">Alert Badge</span></a>
                        </li>
                        <li>
                            <a href="#">Alert Name <span class="label label-primary">Alert Badge</span></a>
                        </li>
                        <li>
                            <a href="#">Alert Name <span class="label label-success">Alert Badge</span></a>
                        </li>
                        <li>
                            <a href="#">Alert Name <span class="label label-info">Alert Badge</span></a>
                        </li>
                        <li>
                            <a href="#">Alert Name <span class="label label-warning">Alert Badge</span></a>
                        </li>
                        <li>
                            <a href="#">Alert Name <span class="label label-danger">Alert Badge</span></a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a href="#">View All</a>
                        </li>
                    </ul>
                </li>
                <% if (login != null){
                	if(login.equals("admin")){%>
                
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-user"></i>  <%=login %>  <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li>
                            <a href="helloWorld.jsp"><i class="fa fa-fw fa-user"></i> Logout </a>
                        </li>                                            
                    </ul>
                </li>
                <%}}
                else{%>               
                 <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-user"></i>  login  <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li>
                            <a href="Login.jsp"><i class="fa fa-fw fa-user"></i> Login </a>
                        </li>                                            
                    </ul>
                </li>                               
                <%} %>
            </ul>
            <!-- Sidebar Menu Items - These collapse to the responsive navigation menu on small screens -->
            <div class="collapse navbar-collapse navbar-ex1-collapse">
                <ul class="nav navbar-nav side-nav">
                    <li class="active">
                        <a href="#"><i class="fa fa-fw fa-dashboard"></i> Home </a>
                    </li>
                    <li>
                        <a href="Consultas.jsp"><i class="fa fa-fw fa-dashboard"></i> Consultas </a>
                    </li>
                    <%if (login.equalsIgnoreCase("admin")){ %>
                    <li>
                        <a href="RetiroIntro.html"><i class="fa fa-fw fa-table"></i> Retiros</a>
                    </li>
                    <li>
                        <a href="Miembros.jsp"><i class="fa fa-fw fa-edit"></i> Miembros</a>
                    </li>
                    <%}else if(tipo.equals("INTERMEDIARIO")){ %>
                    <li>
                        <a href="Autorizar.html"><i class="fa fa-fw fa-table"></i> Autorizar</a>
                    </li>
                    <%}else{ %>
                    <li>
                        <a href="InicioComprar.html"><i class="fa fa-fw fa-bar-chart-o"></i> Comprar </a>
                    </li>
                    <li>
                        <a href="InicioVender.html"><i class="fa fa-fw fa-table"></i> Vender</a>
                    </li>
                    <li>
                        <a href="PortafolioProcesar.html"><i class="fa fa-fw fa-bar-chart-o"></i> Portafolio </a>
                    </li>
                    <%} %>
                    <!--  
                    <li>
                        <a href="bootstrap-elements.html"><i class="fa fa-fw fa-desktop"></i> Bootstrap Elements</a>
                    </li>
                    <li>
                        <a href="bootstrap-grid.html"><i class="fa fa-fw fa-wrench"></i> Bootstrap Grid</a>
                    </li>
                    <li>
                        <a href="javascript:;" data-toggle="collapse" data-target="#demo"><i class="fa fa-fw fa-arrows-v"></i> Dropdown <i class="fa fa-fw fa-caret-down"></i></a>
                        <ul id="demo" class="collapse">
                            <li>
                                <a href="#">Dropdown Item</a>
                            </li>
                            <li>
                                <a href="#">Dropdown Item</a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="blank-page.html"><i class="fa fa-fw fa-file"></i> Blank Page</a>
                    </li>  -->
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </nav>

        <div id="page-wrapper">

            <div class="container-fluid">

                <!-- Page Heading -->
                <div class="row">
                    <div class="col-lg-12">
                        <h1 class="page-header">
                            Consultas ! 
                        </h1>
                       
                    </div>
                </div>

              <!--  Consultar existencias de valores -->
                <h2> Consultar existencias de valores</h2>
                <form action="Consultar.html" method="post" >
                            <div class="form-group input-group">
                            	<br>
                            	<input type="hidden" class="form-control" name="tipoConsulta" value="valores">
                            	<h2>Filtrar por :</h2><br>
                            	<h3> Precio </h3>
                            	<input type="text" class="form-control" name="valor" placeholder="N/A">
                            	 <br>
                            	<h3> Tipo de Valor </h3>
                            	<select class="form-control" name="tipoValor">
                            	<option>N/A</option>
                            	<%Iterator it = tiposValor.iterator();
                            	while(it.hasNext()){ 
                            		String el = (String)(it.next());%>
                            	
                            	<option><%=el %></option>
                            	<% 	
                            	}
                            	 %>
                            	</select >
                                  <br>
                                  <h3> Tipo de Rentabilidad </h3>
                            	<select class="form-control" name="tipoRentabilidad">
                            	<option>N/A</option>
                            	<%Iterator it2 = tiposRentabilidad.iterator();
                            	while(it2.hasNext()){ 
                            		String el = (String)(it2.next());%>
                            	
                            	<option><%=el %></option>
                            	<% 	
                            	}
                            	 %>
                            	</select >
                                  <br> 
                                  <h3> Esta siendo negociado </h3>
                            	<select class="form-control" name="estaDisponible">
									<option>N/A</option>
									<option>Si</option>
									<option>NO</option>
                            	</select >
                                  <br>
                                   <h3> Fecha de expiracion </h3>
                            	<input type="text" class="form-control" name="fechaExpiracion" placeholder="DD/MM/AA" id="datepicker">
                            	  <br>
                            	  <h3> id Oferente </h3>
                            	<input type="text" class="form-control" name="idOferente" placeholder="N/A">
                                  <br>
 <!--                                  <h3> id Intermediario </h3>
                            	<input type="text" class="form-control" name="idIntermediario" placeholder="N/A">
                                  <br>-->
                                  <h3> id Inversionista </h3>
                            	<input type="text" class="form-control" name="idInversionista" placeholder="N/A">
                                  <br>   

                                  <h2> Ordenar por </h2>
                                  
                            	<select class="form-control" name="orden">
									<option>N/A</option>
									<option>Precio</option>
									<option>TipoValor</option>
									<option>TipoRentabilidad</option>
									<option>Negociando</option>
									<option>IdOferente</option>
								<!-- 	<option>IdIntermediario</option>  -->
									<option>IdInversionista</option>
									<option>Fecha</option>
									
									
                            	</select >
                            	<select class="form-control" name="ordenTipo">
									<option>Ascendentemente</option>
									<option>Descendentemente</option>	
                            	</select >
                                  <br><br>



                                <button type="submit" class="btn btn-primary btn-lg">Consultar</button>
                                
                            </div>
                </form>
              <!--                  -->

              <!--  Consultar operaciones de usuarios -->
                <h2> Consultar operaciones de usuarios </h2>

                <form action="Consultar.html" method="post" >
                            <div class="form-group input-group">
                            	<br>
                            	<input type="hidden" class="form-control" name="tipoConsulta" value="operaciones"> 
                            	<h2>Filtrar por :</h2><br>
     <!--                       	<h3> Tipo de Usuario </h3>
                            	<select class="form-control" name="tipoUsuario">
									<option>N/A</option>
									<option>Oferente</option>
									<option>Inversionista</option>
									<option>Intermediario</option>	
                            	</select >
                            	<br> -->  
                            	<h3> Tipo de Operacion </h3>
                            	<select class="form-control" name="tipoOperacion">
									<option>N/A</option>
									<option>COMPRA</option>
									<option>VENTA</option>	
                            	</select >
                            	<br>
                            	<h3> Rango de fechas </h3>
                            	<h4>Desde</h4>
                            	<input type="text" class="form-control" name="fechaDesde">
                            	<h4>Hasta</h4>
                            	<input type="text" class="form-control" name="fechaHasta">
                            	<br>
                            	<h3> Costo </h3>
                            	<input type="text" class="form-control" name="costo">
<!--                             	<br>
                            	<h3> Tipo de Valor </h3>
                            		<select class="form-control" name="tipoValor">
                            	<option>N/A</option>
                            	<% // Iterator it3 = tiposValor.iterator();
                          //  	while(it3.hasNext()){ 
                            //		String el = (String)(it3.next());%>
                            	
                            	<option> </option>
                            	<% 	
                          //  	}
                            	 %>
                            	</select >  -->
                            	<br>
                            	<h3> Tipo de Rentabilidad </h3>
                            	<select class="form-control" name="tipoRentabilidad">
                            	<option>N/A</option>
                            	<%Iterator it4 = tiposRentabilidad.iterator();
                            	while(it4.hasNext()){ 
                            		String el = (String)(it4.next());%>
                            	
                            	<option><%=el %></option>
                            	<% 	
                            	}
                            	 %>
                            	</select >
                            	<br>
                                  
                            	  <br> <br>

                                <button type="submit" class="btn btn-primary btn-lg">Consultar</button>
                                
                            </div>
                </form>
              <!--                  -->

              <!--  Consultar valores con mayor movimiento -->
                <h2> Consultar valores con mayor movimiento en el sistema dentro de un rango de fechas</h2>

                <form action="Consultar.html" method="post" >
                            <div class="form-group input-group">
                            	<br>                            	
                            	<input type="hidden" class="form-control" name="tipoConsulta" value="mayorMovimiento">
                            	<h4> Desde : </h4>
                            	<input type="text" class="form-control" name="fechaDesde">
                            	<br>
                            	<h4> Hasta : </h4>
                            	<input type="text" class="form-control" name="fechaHasta">
                            	<br>
                                
                          

                                <button type="submit" class="btn btn-primary btn-lg">Consultar</button>
                                
                            </div>
                </form>
              <!--                  -->
              <!--  Consultar datos de intermediario mas activo -->
                <h2> Consultar los datos del intermediario mas activo</h2>

                <form action="Consultar.html" method="post" >
                            <div class="form-group input-group">
                            	<input type="hidden" class="form-control" name="tipoConsulta" value="masActivo">
                            	<br>
                            	<h3> Por tipo de valor : </h3>
                            		<select class="form-control" name="tipoValor">
                            	<option>N/A</option>
                            	<%Iterator it5 = tiposValor.iterator();
                            	while(it5.hasNext()){ 
                            		String el = (String)(it5.next());%>
                            	
                            	<option><%=el %></option>
                            	<% 	
                            	}
                            	 %>
                            	</select >
                            	<br>
                            	<h3> O Por valor especifico por su id : </h3>
                            		<input type="text" class="form-control" name="idValor" placeholder="N/A"> <br>
                                
                                <button type="submit" class="btn btn-primary btn-lg">Consultar</button>
                                
                            </div>
                </form>
              <!--                  -->


    

    <!-- /#wrapper -->

    <!-- jQuery Version 1.11.0 -->
    <script src="js/jquery-1.11.0.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="js/bootstrap.min.js"></script>

    <!-- Morris Charts JavaScript -->
    <script src="js/plugins/morris/raphael.min.js"></script>
    <script src="js/plugins/morris/morris.min.js"></script>
    <script src="js/plugins/morris/morris-data.js"></script>

</body>

</html>
                
                