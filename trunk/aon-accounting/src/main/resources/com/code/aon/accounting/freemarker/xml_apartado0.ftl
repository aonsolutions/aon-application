<module id="apartado0" reportingDateStart="${inicioPeriodo(0)}" reportingDateEnd="${finPeriodo(0)}" baseUnit="pure" baseDecimals="0">
    <record id="0100000">
        <item id="0101001">
            <value>NIF</value>
        </item>
        <item id="01010">
            <value>${nifEmpresa}</value>
        </item>
	        <!-- 
		        TODO Si es S.A.
		        <item id="01011"><value>001</value></item>
		        TODO Si es S.L.
		        <item id="01012"><value>001</value></item>
		        TODO Si es Otras
		        <item id="01013"><value>001</value></item>
	        -->
        <item id="0102000">
            <value>DS</value>
        </item>
        <item id="01020">
            <value>${nombreEmpresa}</value>
        </item>
        <item id="0102200">
            <value>01</value>
        </item>
        <item id="01022">
            <value>${direccionEmpresa}</value>
        </item>
        <item id="01023">
            <value>Valdegeña</value>
        </item>
        <item id="0102542">
            <value>42</value>
        </item>
        <item id="01024">
            <value>42111</value>
        </item>
    </record>
    <record id="0103000">
        <record id="0103200">
            <item id="0104000">
                <value>04</value>
            </item>
            <record id="0103300">
                <item id="0102000">
                    <value>DS</value>
                </item>
                <item id="01020">
                    <value>TESTING XBRL LTD.</value>
                </item>
            </record>
        </record>
        <record id="0103100">
            <item id="0106000">
                <value>03</value>
            </item>
            <record id="0103300">
                <item id="0101001">
                    <value>NIF</value>
                </item>
                <item id="01010">
                    <value>A 22222222</value>
                </item>
                <item id="0102000">
                    <value>DS</value>
                </item>
                <item id="01020">
                    <value>TESTING XBRL ESPAÑA, S.A.</value>
                </item>
            </record>
        </record>
    </record>
    <record id="0200000">
        <item id="02009">
            <value>Servicios informáticos</value>
        </item>
    </record>
    <record id="0400000">
        <record id="040000">
            <item id="04001" sign="+" unit="pure" decimals="0">
                <value>120</value>
            </item>
            <item id="04002" sign="+" unit="pure" decimals="0">
                <value>20</value>
            </item>
            <item id="04010" sign="+" unit="pure" decimals="0">
                <value>3</value>
            </item>
        </record>
        <record id="0412000">
            <record id="0412001">
                <item id="04120" sign="+" unit="pure" decimals="0">
                    <value>80</value>
                </item>
                <item id="04121" sign="+" unit="pure" decimals="0">
                    <value>20</value>
                </item>
            </record>
            <record id="0412002">
                <item id="04122" sign="+" unit="pure" decimals="0">
                    <value>10</value>
                </item>
                <item id="04123" sign="+" unit="pure" decimals="0">
                    <value>15</value>
                </item>
            </record>
        </record>
    </record>

    <record id="0110000">
        <item id="0110010">
            <value>2008</value>
        </item>
        <item id="0110020">
            <value>01</value>
        </item>
        <item id="0110030">
            <value>01</value>
        </item>
        <item id="0110040">
            <value>2008</value>
        </item>
        <item id="0110050">
            <value>12</value>
        </item>
        <item id="0110060">
            <value>31</value>
        </item>
        <item id="01901" sign="+" unit="pure" decimals="0">
            <value>2</value>
        </item>
    </record>
    <record id="0900000">
        <item id="09002">
            <value>02</value>
        </item>
    </record>
</module>
	