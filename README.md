# calabash-baxex-standalone-query
Extension de calabash : Lancement d'une XQuery avec BaseX en mode standalone

## Utilisation

Pour utiliser cette extension, basex doit être installé sur votre poste de travail

- Dans votre xproc :
	- Définir le namespace suivant :
```
xmlns:bxs="http://arkamy/xml/calabash-baxex-standalone-query"
```
	- Importer la librairie src/main/resources/basex-xquery-lib.xpl : 
```
<p:import href="<path>/basex-xquery-lib.xpl"/>
```
	- Lancer une xquery via basex via l'instruction suivante :
```
	<bxs:query>
		<p:input port="source">
			...
		</p:input>
		<p:input port="query">
			...
		</p:input>
	</bxs:query>
```
		- Le résultat de la xquery est encapsulé dans l'élément suivant :  
```
<c:result xmlns:c='http://www.w3.org/ns/xproc-step'>
```
- N'oubliez pas de positionner le jar de ce projet dans votre classpath