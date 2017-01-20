<?xml version="1.0" encoding="UTF-8"?>
<p:library xmlns:p="http://www.w3.org/ns/xproc"
	xmlns:bxs="http://arkamy/xml/calabash-baxex-standalone-query"
	xmlns:pkg="http://expath.org/ns/pkg" version="1.0">

	<!-- Step to launch a XQuery in BaseX standalone -->

	<p:declare-step type="bxs:query">
		<p:input port="source" sequence="true"/>
		<p:input port="query"/>
		<p:output port="result" primary="true"/>
	</p:declare-step>

</p:library>
