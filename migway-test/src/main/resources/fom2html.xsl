<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:fom="http://standards.ieee.org/IEEE1516-2010"
	version="1.0">
<xsl:template match="fom:objectModel">
  <h2>Object model de type = <xsl:value-of select="fom:modelIdentification/fom:type" /></h2>
  <ul>
    <li><a href="#LSTOBJ">Liste des objets</a></li>
    <li><a href="#LSTBAS">Liste des types basiques</a></li>
    <li><a href="#LSTSPL">Liste des types simples</a></li>
    <li><a href="#LSTENU">Liste des types enum</a></li>
    <li><a href="#LSTTBL">Liste des types tabulaires</a></li>
    <li><a href="#LSTSTR">Liste des types structures</a></li>
    <li><a href="#LSTVAR">Liste des types "j'en sais rien"</a></li>
  </ul>
  <h3><a name="LSTOBJ">Liste des objets: </a></h3>
  <xsl:apply-templates select="fom:objects" />
  <h3><a name="LSTBAS">Liste des types basiques:</a></h3>
  <xsl:apply-templates select="fom:dataTypes/fom:basicDataRepresentations" />
  <h3><a name="LSTSPL">Liste des types simples:</a></h3>
  <xsl:apply-templates select="fom:dataTypes/fom:simpleDataTypes" />
  <h3><a name="LSTENU">Liste des types Enum:</a></h3>
  <xsl:apply-templates select="fom:dataTypes/fom:enumeratedDataTypes" />
  <h3><a name="LSTTBL">Liste des types tables:</a></h3>
  <xsl:apply-templates select="fom:dataTypes/fom:arrayDataTypes" />
  <h3><a name="LSTSTR">Liste des types structures:</a></h3>
  <xsl:apply-templates select="fom:dataTypes/fom:fixedRecordDataTypes" />
  <h3><a name="LSTVAR">Liste des types "j'en sais rien"!:</a></h3>
  <xsl:apply-templates select="fom:dataTypes/fom:variantRecordDataTypes" />
  
  <hr />
  <!-- détail des classes -->
  <xsl:apply-templates select="fom:objects//fom:objectClass" mode="detail" />
</xsl:template>
<!-- les groupes (structures intermédiaires ou de métadonnées) -->
<xsl:template match="fom:modelIdentification">modelIdentification</xsl:template>

<xsl:template match="fom:objects">
  <xsl:apply-templates select="." mode="htree" />
  <table>
    <tr><th>Name</th><th>Sémantique</th><th>Parent</th></tr>
    <xsl:apply-templates select="fom:objectClass"  />
  </table>
</xsl:template>

<xsl:template match="fom:objects" mode="vtree">
  <h4>Arbre d'héritage</h4>
  <table id="vtree">
    <tr><xsl:apply-templates select="fom:objectClass" mode="vtree" /></tr>
  </table>
</xsl:template>

<xsl:template match="fom:objects" mode="htree">
  <h4>Arbre d'héritage</h4>
  <table id="htree">
    <xsl:apply-templates select="fom:objectClass" mode="htree" />
  </table>
</xsl:template>

<xsl:template match="fom:dataTypes/*">
  <table>
    <tr><th>Name</th><th>Représentation</th><th>Sémantique</th></tr>
    <xsl:apply-templates />
  </table>
</xsl:template>
<!-- Les types -->
<xsl:template match="fom:simpleData">
    <tr>
      <td><xsl:element name="a"><xsl:attribute name="name"><xsl:value-of select="fom:name" /></xsl:attribute><xsl:value-of select="fom:name" /></xsl:element></td>
      <td><xsl:element name="a"><xsl:attribute name="href">#<xsl:value-of select="fom:representation" /></xsl:attribute><xsl:value-of select="fom:representation" /></xsl:element><b><xsl:text> (</xsl:text><xsl:value-of select="fom:units" /><xsl:text>)</xsl:text></b></td>
      <td><xsl:value-of select="fom:semantics" /></td>
    </tr>
</xsl:template>

<xsl:template match="fom:arrayData">
  <tr>
    <td><xsl:element name="a"><xsl:attribute name="name"><xsl:value-of select="fom:name" /></xsl:attribute><xsl:value-of select="fom:name" /></xsl:element></td>
    <td><xsl:element name="a"><xsl:attribute name="href">#<xsl:value-of select="fom:dataType" /></xsl:attribute><xsl:value-of select="fom:dataType" /></xsl:element><b><xsl:text> (</xsl:text><xsl:value-of select="fom:cardinality" /><xsl:text>)</xsl:text></b></td>
    <td><xsl:value-of select="fom:semantics" /></td>
  </tr>
</xsl:template>

<xsl:template match="fom:fixedRecordData">
  <tr>
    <td><xsl:element name="a"><xsl:attribute name="name"><xsl:value-of select="fom:name" /></xsl:attribute><xsl:value-of select="fom:name" /></xsl:element></td>
    <td>
      <xsl:for-each select="fom:field">
        <xsl:value-of select="fom:name" /> [<xsl:element name="a"><xsl:attribute name="href">#<xsl:value-of select="fom:dataType" /></xsl:attribute><xsl:value-of select="fom:dataType" /></xsl:element>] (<xsl:value-of select="fom:semantics" />)<br />
      </xsl:for-each></td>
    <td><xsl:value-of select="fom:semantics" /></td>
  </tr>
</xsl:template>

<xsl:template match="fom:variantRecordData">
  <tr>
    <td><xsl:element name="a"><xsl:attribute name="name"><xsl:value-of select="fom:name" /></xsl:attribute><xsl:value-of select="fom:name" /></xsl:element></td>
    <td><xsl:element name="a"><xsl:attribute name="href">#<xsl:value-of select="fom:dataType" /></xsl:attribute><xsl:value-of select="fom:dataType" /></xsl:element></td>
    <td><xsl:value-of select="fom:semantics" /></td>
  </tr>
</xsl:template>

<xsl:template match="fom:enumeratedData">
  <tr>
    <td><xsl:element name="a"><xsl:attribute name="name"><xsl:value-of select="fom:name" /></xsl:attribute><xsl:value-of select="fom:name" /></xsl:element></td>
    <td><xsl:element name="a"><xsl:attribute name="href">#<xsl:value-of select="fom:representation" /></xsl:attribute><xsl:value-of select="fom:representation" /></xsl:element> (<xsl:value-of select="count(fom:enumerator)" />)</td>
    <td><xsl:value-of select="fom:semantics" /></td>
  </tr>
</xsl:template>

<xsl:template match="fom:basicData">
  <tr>
    <td><xsl:element name="a"><xsl:attribute name="name"><xsl:value-of select="fom:name" /></xsl:attribute><xsl:value-of select="fom:name" /></xsl:element></td>
    <td><xsl:value-of select="fom:encoding" /><b><xsl:text> (</xsl:text><xsl:value-of select="fom:size" /><xsl:text>)</xsl:text></b></td>
    <td><xsl:value-of select="fom:interpretation" /></td>
  </tr>
</xsl:template>

<!-- Les objets -->      
<xsl:template match="fom:objectClass">
  <tr>
    <td>
      <xsl:element name="a">
        <xsl:attribute name="href"><xsl:text>#</xsl:text><xsl:value-of select="fom:name" />
        </xsl:attribute>
        <xsl:value-of select="fom:name" />
      </xsl:element>
    </td>
    <td><xsl:value-of select="fom:semantics" /></td>
    <td><xsl:value-of select="../fom:name" /></td>
  </tr>
  <xsl:apply-templates select="fom:objectClass" />
</xsl:template>

<xsl:template match="fom:objectClass" mode="htree">
  <tr>
    <td>
      <xsl:element name="a"><xsl:attribute name="href">#<xsl:value-of select="fom:name" /></xsl:attribute><xsl:value-of select="fom:name" /></xsl:element>
    </td>
    <xsl:if test="count(fom:objectClass)!=0">
    <td>
      <table>
        <xsl:apply-templates select="fom:objectClass" mode="htree" />
      </table>
    </td>
    </xsl:if>
  </tr>
</xsl:template>

<xsl:template match="fom:objectClass" mode="vtree">
  <td>
    <xsl:element name="a"><xsl:attribute name="href">#<xsl:value-of select="fom:name" /></xsl:attribute><xsl:value-of select="fom:name" /></xsl:element>
    <xsl:if test="count(fom:objectClass)!=0">
      <table>
        <tr>
          <table>
            <xsl:apply-templates select="fom:objectClass" mode="vtree" />
          </table>
        </tr>
      </table>
    </xsl:if>
  </td>
</xsl:template>

<xsl:template match="fom:objectClass" mode="detail">
    <table class="objDetail">
    <tr>
      <th>Name</th>
      <td class="detailName"><xsl:element name="a"><xsl:attribute name="name"><xsl:value-of select="fom:name" /></xsl:attribute></xsl:element><xsl:value-of select="fom:name" /></td>
      <td class="parentClass"><xsl:element name="a"><xsl:attribute name="href"><xsl:text>#</xsl:text><xsl:value-of select="../fom:name" /></xsl:attribute><xsl:value-of select="../fom:name" /></xsl:element></td>
    </tr>
    <xsl:for-each select="fom:attribute">
    <tr>
      <th><xsl:value-of select="fom:name" /></th>
        <td><xsl:element name="a"><xsl:attribute name="href"><xsl:text>#</xsl:text><xsl:value-of select="fom:dataType" /></xsl:attribute><xsl:value-of select="fom:dataType" /></xsl:element></td>
        <td><xsl:value-of select="fom:semantics" /></td>
      </tr>
    </xsl:for-each>
  </table>
</xsl:template>

<xsl:template match="/">
    <html>
			<head>
        <style>
        <xsl:text>
          table { 
            border: solid 1px black; 
            padding: 0px; 
            border-bottom: none;
          }
          td {
            border-bottom: dotted 1px blue;
          }
          th {
            border-bottom: solid 1px black;  
          }
          table, td, th, tr { margin: 0px;}
          table.objDetail { 
            border: solid 4px blue;
            margin: 4px;
          }
          table.objDetail td.detailName { background-color: yellow; }
          table#vtree, #vtree table {
            border: none;
            margin: 0px;
            padding: 0px;
          }
          #vtree td {
            text-align: center;
            margin: 0px;
            padding: 2px;
            border-top: dotted 1px red;
            border-right: dotted 1px blue;
            border-bottom: none;
          }
          #htree td {
            padding: 0px;
            padding-left: 5px;
            padding-bottom: 0px;
            margin: 0px;
            /*border-left: dotted 1px red;*/
          }
          table#htree, #htree table {
            border: none;
            border-left: dotted 1px red;
            vertical-align: middle;
            width: 100%;
          }
        </xsl:text>
        </style>
      </head>
			<body>
        <hr />
				<xsl:apply-templates select="fom:objectModel" />
			</body>
		</html>
</xsl:template>
</xsl:stylesheet>