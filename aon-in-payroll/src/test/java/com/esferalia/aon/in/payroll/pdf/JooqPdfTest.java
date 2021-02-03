package com.esferalia.aon.in.payroll.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.jooq.JooqPDFSalaryBuilder;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;
@Ignore
public class JooqPdfTest {

	@Test
	@Ignore
	public void testA3() throws IOException, UnknownPDFException, SQLException {
		try (InputStream is = PdfTest.class.getResourceAsStream("year_payrolls_lorena.pdf");
				Connection connection = DriverManager
						.getConnection("jdbc:mysql://172.17.0.2:3306/test-aonsolutions-org", "root", "root");
				AONContext aonContext = new AONContext(connection)) {
			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
					"payroll-test.aonsolutions.org");
			SalaryPDFParser.parse(is, builder);
			builder.execute();
		}
		try (InputStream is = PdfTest.class.getResourceAsStream("year_payrolls_lorena.pdf");
				Connection connection = DriverManager
						.getConnection("jdbc:mysql://172.17.0.2:3306/test-aonsolutions-org", "root", "root")) {
			SalaryPDFParser.parse(is, new SalaryBuilder() {

				private String empName, empAddress, empCity, entDoc, entName, entAddress, ccc, cat, dni;
				private Date senDate, startDate, endDate;

				@Override
				public void setEmployeeName(String employeeName) {
					empName = employeeName;
				}

				@Override
				public void setEmployeeAddress(String employeeAddress) {
					empAddress = employeeAddress;
				}

				@Override
				public void setEmployeeCity(String employeeCity) {
					empCity = employeeCity;
				}

				@Override
				public void setEnterpriseDocument(String enterpriseDocument) {
					entDoc = enterpriseDocument;
				}

				@Override
				public void setEnterpriseName(String enterpriseName) {
					entName = enterpriseName;
				}

				@Override
				public void setEnterpriseAddress(String enterpriseAddress) {
					entAddress = enterpriseAddress;
				}

				@Override
				public void setCcc(String ccc) {
					this.ccc = ccc;
				}

				@Override
				public void setCategory(String category) {
					cat = category;
				}

				@Override
				public void setSeniorityDate(Date seniorityDate) {
					senDate = seniorityDate;
				}

				@Override
				public void setEmployeeDocument(String employeeDocument) {
					dni = employeeDocument;
					try {
						String sql = "select distinct UPPER(p1.name) as name, UPPER(p1.first_surname) as first, UPPER(p1.second_surname) as second from person p1, registry r1 where p1.registry=r1.id and r1.document='"
								+ dni + "'";
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							assertEquals(
									rs.getString("first") + " " + rs.getString("second") + ", " + rs.getString("name"),
									empName);
						}
						sql = "select distinct enterprise_name, enterprise_address, enterprise_document, ccc, category, seniority_date from salary where employee_document = '"
								+ dni + "'";
						st = connection.createStatement();
						rs = st.executeQuery(sql);
						HashSet<String> cats = new HashSet<String>();
						while (rs.next()) {
							assertEquals(rs.getString("enterprise_name"), entName);
							assertEquals(rs.getString("enterprise_address"), entAddress);
							assertEquals(rs.getString("enterprise_document"), entDoc);
							assertEquals(rs.getString("ccc"), ccc);
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
							assertEquals(rs.getString("seniority_date"), df.format(senDate));
							cats.add(rs.getString("category"));
						}
						if (!cats.contains(cat))
							fail("Category fail");
					} catch (SQLException e) {
						fail("SQL exception");
					}
				}

				@Override
				public void setSocialSecurityNumber(String socialSecurityNumber) {
					String sql = "select distinct social_security_number as nss from salary where employee_document = '"
							+ dni + "'";
					Statement st;
					try {
						st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							assertEquals(rs.getString("nss"), socialSecurityNumber);
						}
					} catch (SQLException e) {
						fail("SQL exception");
					}

				}

				@Override
				public void setQuoteGroup(String quoteGroup) {
					String sql = "select distinct quote_group as qg from salary where employee_document = '" + dni
							+ "'";
					Statement st;
					try {
						st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							assertEquals(rs.getString("qg"), quoteGroup);
						}
					} catch (SQLException e) {
						fail("SQL exception");
					}
				}

				@Override
				public void setStartDate(Date startDate) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String dateStr = df.format(startDate);
					this.startDate = startDate;
					String sql = "select count(*) as c from salary where employee_document='" + dni
							+ "' and start_date=str_to_date('" + dateStr + "','%Y-%m-%d')";
					// String sql = "select distinct start_date as sd from salary where
					// employee_document='"+dni+"'";
					Statement st;
					try {
						st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							if (rs.getInt("c") != 1) {
								fail("Start date fail");
							}
						}
					} catch (SQLException e) {
						fail("SQL exception");
					}
				}

				@Override
				public void setEndDate(Date endDate) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String dateStr = df.format(endDate);
					this.endDate = endDate;
					String sql = "select count(*) as c from salary where employee_document='" + dni
							+ "' and end_date=str_to_date('" + dateStr + "','%Y-%m-%d')";
					// String sql = "select distinct start_date as sd from salary where
					// employee_document='"+dni+"'";
					Statement st;
					try {
						st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							if (rs.getInt("c") != 1) {
								fail("End date fail");
							}
						}
					} catch (SQLException e) {
						fail("SQL exception");
					}
				}

				@Override
				public void setTimeUnits(Integer timeUnits) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);
					String sql = "select time_units as tu from salary where start_date=str_to_date('" + sDate
							+ "','%Y-%m-%d') and end_date=str_to_date('" + eDate
							+ "','%Y-%m-%d') and employee_document='" + dni + "'";
					Statement st;
					try {
						st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							if (rs.getInt("tu") != timeUnits) {
								fail("Time units fail");
							}
						}
					} catch (SQLException e) {
						fail("SQL exception");
					}

				}

				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
						Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select count(*) c from salary_payment sp where exists "
							+ "(select id from salary where id  = sp.salary and " + "start_date=str_to_date('" + sDate
							+ "','%Y-%m-%d') and " + "end_date=str_to_date('" + eDate + "','%Y-%m-%d') and "
							+ "employee_document='" + dni + "') and " + "payment_concept='" + payment.getName()
							+ "' and " + "amount=" + amount;

					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							if (rs.getInt("c") != 1) {
								fail("payment does not exist");
							}
						}
					} catch (SQLException e) {
						fail("Payment fail");
					}

				}

				@Override
				public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
						Map<String, ITimedVariable<?>> context) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select count(*) c from salary_deduction sd where exists "
							+ "(select id from salary where id  = sd.salary and " + "start_date=str_to_date('" + sDate
							+ "','%Y-%m-%d') and end_date=str_to_date('" + eDate + "','%Y-%m-%d') and "
							+ "employee_document='" + dni + "') and deduction_concept='" + deduction.getName()
							+ "' and " + "amount=" + amount;
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							if (rs.getInt("c") != 1) {
								fail("payment does not exist");
							}
						}
					} catch (SQLException e) {
						fail("Payment sql fail");
					}

				}

				@Override
				public void setTotalPayment(Double totalPayment) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select total_payment from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (rs.getDouble("total_payment") != totalPayment) {
								fail("total payment fail");
							}
						} else {
							fail("total payment fail");
						}
					} catch (SQLException e) {
						fail("total payment sql fail");
					}

				}

				@Override
				public void setProExtBase(Double extraPayProration) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select pro_ext_base from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (extraPayProration == null)
								extraPayProration = 0.0;
							if (rs.getDouble("pro_ext_base") != extraPayProration) {
								fail("ProExtBase fail");
							}
						} else {
							fail("ProExtBase fail");
						}
					} catch (SQLException e) {
						fail("ProExtBase sql fail");
					}
				}

				@Override
				public void setCgcBase(Double commonBase) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select cgc_base from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (commonBase == null)
								commonBase = 0.0;
							if (rs.getDouble("cgc_base") != commonBase) {
								fail("Cgc Base fail");
							}
						} else {
							fail("Cgc Base fail");
						}
					} catch (SQLException e) {
						fail("Cgc Base sql fail");
					}
				}

				@Override
				public void setCgpBase(Double professionalBase) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select cgp_base from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (professionalBase == null)
								professionalBase = 0.0;
							if (rs.getDouble("cgp_base") != professionalBase) {
								fail("CgpBase fail");
							}
						} else {
							fail("CgpBase fail");
						}
					} catch (SQLException e) {
						fail("CgpBase sql fail");
					}
				}

				@Override
				public void setTotalIrpf(Double totalIrpf) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select total_irpf from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (totalIrpf == null)
								totalIrpf = 0.0;
							if (rs.getDouble("total_irpf") != totalIrpf) {
								fail("total Irpf fail");
							}
						} else {
							fail("total Irpf fail");
						}
					} catch (SQLException e) {
						fail("total Irpf sql fail");
					}
				}

				@Override
				public void setTotalDeduction(Double totalDeduction) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select total_deduction from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (totalDeduction == null)
								totalDeduction = 0.0;
							if (rs.getDouble("total_deduction") != totalDeduction) {
								fail("total payment fail");
							}
						} else {
							fail("total deduction fail");
						}
					} catch (SQLException e) {
						fail("total deduction sql fail");
					}
				}

				@Override
				public void setIssueDate(Date issueDate) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);
					String iDate = df.format(issueDate);

					String sql = "select issue_date from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {

							if (!iDate.equals(rs.getString("issue_date"))) {
								fail("Issue date fail");
							}
						} else {
							fail("Issue date fail");
						}
					} catch (SQLException e) {
						fail("Issue date sql fail");
					}
				}

				@Override
				public void setTotalLiquid(Double totalLiquid) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select total_liquid from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (totalLiquid == null)
								totalLiquid = 0.0;
							if (rs.getDouble("total_liquid") != totalLiquid) {
								fail("total liquid fail");
							}
						} else {
							fail("total liquid fail");
						}
					} catch (SQLException e) {
						fail("total payment sql fail");
					}
				}

				@Override
				public void addCost(Double amount, String description, Date start, Date end, IDeduction cost,
						Map<String, ITimedVariable<?>> context) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select count(*) c from salary_cost sd where exists "
							+ "(select id from salary where id  = sd.salary and " + "start_date=str_to_date('" + sDate
							+ "','%Y-%m-%d') and end_date=str_to_date('" + eDate + "','%Y-%m-%d') and "
							+ "employee_document='" + dni + "') and cost_concept='" + cost.getName() + "' and "
							+ "amount=" + amount;
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (rs.getInt("c") != 1) {
								fail("cost fail");
							}
						} else {
							fail("cost fail");
						}
					} catch (SQLException e) {
						fail("cost sql fail");
					}

				}

				@Override
				public void addData(String name, ITimedVariable<?> data) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select count(*) c from salary_data sd where exists "
							+ "(select id from salary where id  = sd.salary and " + "start_date=str_to_date('" + sDate
							+ "','%Y-%m-%d') and end_date=str_to_date('" + eDate + "','%Y-%m-%d') and "
							+ "employee_document='" + dni + "') and name='" + name + "' and "
							+ "start_date=str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date=str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (rs.getInt("c") != 1) {
								if (!(name.equals("BASE_IRPF") || name.equals("TOTAL_DEVENGADO")
										|| name.equals("CGC_E")))
									fail("data fail");

								// CONCEPTS IGNORED ON THE IF CLAUSE ARE IGNORED WHEN INSERTING

							}
						} else {
							fail("data fail");
						}
					} catch (SQLException e) {
						fail("data sql fail");
					}
				}

// 'contract', 'person', 'enterprise', 'salary' , 'salary_payment' y
// 'salary_deduction'
			});

		}
	}

	@Test
	@Ignore
	public void testDsiAt() throws IOException, UnknownPDFException, SQLException {
		try (InputStream is = PdfTest.class.getResourceAsStream("dsi_at.pdf");
				Connection connection = DriverManager
						.getConnection("jdbc:mysql://172.17.0.2:3306/test-aonsolutions-org", "root", "root");
				AONContext aonContext = new AONContext(connection)) {
			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
					"payroll-test.aonsolutions.org");
			SalaryPDFParser.parse(is, builder);
			builder.execute();
		}
		try (InputStream is = PdfTest.class.getResourceAsStream("dsi_at.pdf");
				Connection connection = DriverManager
						.getConnection("jdbc:mysql://172.17.0.2:3306/test-aonsolutions-org", "root", "root")) {
			SalaryPDFParser.parse(is, new SalaryBuilder() {

				private String empName, empAddress, empCity, entName, entAddress, cat, dni;
				private Date senDate, startDate, endDate;
				private int timeUnits;

				@Override
				public void setEnterpriseName(String enterpriseName) {
					entName = enterpriseName;
				}

				@Override
				public void setEmployeeName(String employeeName) {
					empName = employeeName;
				}

				@Override
				public void setEnterpriseAddress(String enterpriseAddress) {
					entAddress = enterpriseAddress;
				}

				@Override
				public void setEmployeeDocument(String employeeDocument) {
					dni = employeeDocument;
					try {
						String sql = "select distinct UPPER(p1.name) as name, UPPER(p1.first_surname) as first, UPPER(p1.second_surname) as second from person p1, registry r1 where p1.registry=r1.id and r1.document='"
								+ dni + "'";
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							assertEquals(
									rs.getString("first") + " " + rs.getString("second") + ", " + rs.getString("name"),
									empName);
						}
						sql = "select distinct enterprise_name, enterprise_address from salary where employee_document = '"
								+ dni + "'";
						st = connection.createStatement();
						rs = st.executeQuery(sql);
						while (rs.next()) {
							assertEquals(rs.getString("enterprise_name"), entName);
							assertEquals(rs.getString("enterprise_address"), entAddress);
						}
					} catch (SQLException e) {
						fail("SQL exception");
					}
				}

				/*
				 * @Override public void setEmployeeAddress(String employeeAddress) {
				 * 
				 * String sql =
				 * "select distinct enterprise_name, enterprise_address from salary where employee_document = '"
				 * + dni + "'"; try { Statement st = connection.createStatement(); ResultSet rs
				 * = st.executeQuery(sql); } catch (SQLException e) { // TODO Auto-generated
				 * catch block e.printStackTrace(); }
				 * 
				 * 
				 * empAddress = employeeAddress; }
				 */

				@Override
				public void setEmployeeCity(String employeeCity) {
					empCity = employeeCity;
				}

				@Override
				public void setEnterpriseDocument(String enterpriseDocument) {
					String sql = "select distinct enterprise_document from salary where employee_document = '" + dni
							+ "'";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							assertEquals(rs.getString("enterprise_document"), enterpriseDocument);
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void setCcc(String ccc) {
					String sql = "select distinct ccc from salary where employee_document = '" + dni + "'";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							assertEquals(rs.getString("ccc"), ccc);
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void setCategory(String category) {
					String sql = "select distinct category from salary where employee_document = '" + dni + "'";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							assertEquals(rs.getString("category"), category);
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void setSeniorityDate(Date seniorityDate) {

					String sql = "select distinct seniority_date sd from salary where employee_document = '" + dni
							+ "'";
					Statement st;
					try {
						st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);

						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						if (rs.next())
							assertEquals(rs.getString("sd"), df.format(seniorityDate));
						else
							fail("No results for seniority date");

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				@Override
				public void setSocialSecurityNumber(String socialSecurityNumber) {
					String sql = "select distinct social_security_number as nss from salary where employee_document = '"
							+ dni + "'";
					Statement st;
					try {
						st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							assertEquals(rs.getString("nss"), socialSecurityNumber);
						}
					} catch (SQLException e) {
						fail("SQL exception");
					}

				}

				@Override
				public void setQuoteGroup(String quoteGroup) {
					String sql = "select distinct quote_group as qg from salary where employee_document = '" + dni
							+ "'";
					Statement st;
					try {
						st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							assertEquals(rs.getString("qg"), quoteGroup);
						}
					} catch (SQLException e) {
						fail("SQL exception");
					}
				}

				@Override
				public void setStartDate(Date startDate) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String dateStr = df.format(startDate);
					this.startDate = startDate;
					String sql = "select count(*) as c from salary where employee_document='" + dni
							+ "' and start_date=str_to_date('" + dateStr + "','%Y-%m-%d')";
					// String sql = "select distinct start_date as sd from salary where
					// employee_document='"+dni+"'";
					Statement st;
					try {
						st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							if (rs.getInt("c") != 1) {
								fail("Start date fail");
							}
						}
					} catch (SQLException e) {
						fail("SQL exception");
					}
				}

				@Override
				public void setEndDate(Date endDate) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String dateStr = df.format(endDate);
					this.endDate = endDate;
					String sql = "select count(*) as c from salary where employee_document='" + dni
							+ "' and end_date=str_to_date('" + dateStr + "','%Y-%m-%d')";
					// String sql = "select distinct start_date as sd from salary where
					// employee_document='"+dni+"'";
					Statement st;
					try {
						st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							if (rs.getInt("c") != 1) {
								fail("End date fail");
							}
						}

						String sDate = df.format(startDate);
						String eDate = df.format(endDate);
						sql = "select time_units as tu from salary where start_date=str_to_date('" + sDate
								+ "','%Y-%m-%d') and end_date=str_to_date('" + eDate
								+ "','%Y-%m-%d') and employee_document='" + dni + "'";
						st = connection.createStatement();
						rs = st.executeQuery(sql);
						while (rs.next()) {
							if (rs.getInt("tu") != timeUnits) {
								fail("Time units fail");
							}
						}

					} catch (SQLException e) {
						fail("SQL exception");
					}
				}

				@Override
				public void setTimeUnits(Integer timeUnits) {
					this.timeUnits = timeUnits;
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String sDate = df.format(startDate);
//					String eDate = df.format(endDate);
//					String sql = "select time_units as tu from salary where start_date=str_to_date('" + sDate
//							+ "','%Y-%m-%d') and end_date=str_to_date('" + eDate
//							+ "','%Y-%m-%d') and employee_document='" + dni + "'";
//					Statement st;
//					try {
//						st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						while (rs.next()) {
//							if (rs.getInt("tu") != timeUnits) {
//								fail("Time units fail");
//							}
//						}
//					} catch (SQLException e) {
//						fail("SQL exception");
//					}

				}

				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
						Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);
					String sql;
					if (payment.getName() != null) {
						sql = "select count(*) c from salary_payment sp where exists "
								+ "(select id from salary where id  = sp.salary and " + "start_date=str_to_date('"
								+ sDate + "','%Y-%m-%d') and " + "end_date=str_to_date('" + eDate + "','%Y-%m-%d') and "
								+ "employee_document='" + dni + "') and " + "payment_concept='" + payment.getName()
								+ "' and " + "amount=" + amount;
					} else {
						sql = "select count(*) c from salary_payment sp where exists "
								+ "(select id from salary where id  = sp.salary and " + "start_date=str_to_date('"
								+ sDate + "','%Y-%m-%d') and " + "end_date=str_to_date('" + eDate + "','%Y-%m-%d') and "
								+ "employee_document='" + dni + "') and " + "payment_concept is null and " + "amount="
								+ amount;
					}
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							if (rs.getInt("c") != 1) {
								fail("payment does not exist");
							}
						}
					} catch (SQLException e) {
						fail("Payment fail");
					}

				}

				@Override
				public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
						Map<String, ITimedVariable<?>> context) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select count(*) c from salary_deduction sd where exists "
							+ "(select id from salary where id  = sd.salary and " + "start_date=str_to_date('" + sDate
							+ "','%Y-%m-%d') and end_date=str_to_date('" + eDate + "','%Y-%m-%d') and "
							+ "employee_document='" + dni + "') and deduction_concept='" + deduction.getName()
							+ "' and " + "amount=" + amount;
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						while (rs.next()) {
							if (rs.getInt("c") != 1) {
								fail("payment does not exist");
							}
						}
					} catch (SQLException e) {
						fail("Payment sql fail");
					}

				}

				@Override
				public void setTotalPayment(Double totalPayment) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select total_payment from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (rs.getDouble("total_payment") != totalPayment) {
								fail("total payment fail");
							}
						} else {
							fail("total payment fail");
						}
					} catch (SQLException e) {
						fail("total payment sql fail");
					}

				}

				@Override
				public void setProExtBase(Double extraPayProration) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select pro_ext_base from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (extraPayProration == null)
								extraPayProration = 0.0;
							if (rs.getDouble("pro_ext_base") != extraPayProration) {
								fail("ProExtBase fail");
							}
						} else {
							fail("ProExtBase fail");
						}
					} catch (SQLException e) {
						fail("ProExtBase sql fail");
					}
				}

				@Override
				public void setCgcBase(Double commonBase) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select cgc_base from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (commonBase == null)
								commonBase = 0.0;
							if (rs.getDouble("cgc_base") != commonBase) {
								fail("Cgc Base fail");
							}
						} else {
							fail("Cgc Base fail");
						}
					} catch (SQLException e) {
						fail("Cgc Base sql fail");
					}
				}

				@Override
				public void setCgpBase(Double professionalBase) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select cgp_base from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (professionalBase == null)
								professionalBase = 0.0;
							if (rs.getDouble("cgp_base") != professionalBase) {
								fail("CgpBase fail");
							}
						} else {
							fail("CgpBase fail");
						}
					} catch (SQLException e) {
						fail("CgpBase sql fail");
					}
				}

				@Override
				public void setTotalIrpf(Double totalIrpf) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select total_irpf from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (totalIrpf == null)
								totalIrpf = 0.0;
							if (rs.getDouble("total_irpf") != totalIrpf) {
								fail("total Irpf fail");
							}
						} else {
							fail("total Irpf fail");
						}
					} catch (SQLException e) {
						fail("total Irpf sql fail");
					}
				}

				@Override
				public void setTotalDeduction(Double totalDeduction) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select total_deduction from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (totalDeduction == null)
								totalDeduction = 0.0;
							if (rs.getDouble("total_deduction") != totalDeduction) {
								fail("total payment fail");
							}
						} else {
							fail("total deduction fail");
						}
					} catch (SQLException e) {
						fail("total deduction sql fail");
					}
				}

				@Override
				public void setIssueDate(Date issueDate) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);
					String iDate = df.format(issueDate);

					String sql = "select issue_date from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {

							if (!iDate.equals(rs.getString("issue_date"))) {
								fail("Issue date fail");
							}
						} else {
							fail("Issue date fail");
						}
					} catch (SQLException e) {
						fail("Issue date sql fail");
					}
				}

				@Override
				public void setTotalLiquid(Double totalLiquid) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select total_liquid from salary where employee_document = '" + dni + "' and "
							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (totalLiquid == null)
								totalLiquid = 0.0;
							if (rs.getDouble("total_liquid") != totalLiquid) {
								fail("total liquid fail");
							}
						} else {
							fail("total liquid fail");
						}
					} catch (SQLException e) {
						fail("total payment sql fail");
					}
				}

				@Override
				public void addCost(Double amount, String description, Date start, Date end, IDeduction cost,
						Map<String, ITimedVariable<?>> context) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select count(*) c from salary_cost sd where exists "
							+ "(select id from salary where id  = sd.salary and " + "start_date=str_to_date('" + sDate
							+ "','%Y-%m-%d') and end_date=str_to_date('" + eDate + "','%Y-%m-%d') and "
							+ "employee_document='" + dni + "') and cost_concept='" + cost.getName() + "' and "
							+ "amount=" + amount;
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (rs.getInt("c") != 1) {
								fail("cost fail");
							}
						} else {
							fail("cost fail");
						}
					} catch (SQLException e) {
						fail("cost sql fail");
					}

				}

				@Override
				public void addData(String name, ITimedVariable<?> data) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String sDate = df.format(startDate);
					String eDate = df.format(endDate);

					String sql = "select count(*) c from salary_data sd where exists "
							+ "(select id from salary where id  = sd.salary and " + "start_date=str_to_date('" + sDate
							+ "','%Y-%m-%d') and end_date=str_to_date('" + eDate + "','%Y-%m-%d') and "
							+ "employee_document='" + dni + "') and name='" + name + "' and "
							+ "start_date=str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date=str_to_date('"
							+ eDate + "', '%Y-%m-%d')";
					try {
						Statement st = connection.createStatement();
						ResultSet rs = st.executeQuery(sql);
						if (rs.next()) {
							if (rs.getInt("c") != 1) {
								if (!(name.equals("BASE_IRPF") || name.equals("TOTAL_DEVENGADO")
										|| name.equals("CGC_E")))
									fail("data fail");

							}
						} else {
							fail("data fail");
						}
					} catch (SQLException e) {
						fail("data sql fail");
					}
				}

// 'contract', 'person', 'enterprise', 'salary' , 'salary_payment' y
// 'salary_deduction'
			});

		}
	}
	
	
	@Test
	@Ignore
	public void testA3Finiquito() throws IOException, UnknownPDFException, SQLException {
		try (InputStream is = PdfTest.class.getResourceAsStream("nominaA3Finiquito.pdf");
				Connection connection = DriverManager
						.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
				AONContext aonContext = new AONContext(connection)) {
			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
					"grupoayudat.aonsolutions.net");
			SalaryPDFParser.parse(is, builder);
			builder.execute();
		}
	}
	
	
	@Test
	//@Ignore
	public void testA3ALot() throws IOException, UnknownPDFException, SQLException {
		try (InputStream is = PdfTest.class.getResourceAsStream("learning2020.pdf");
				Connection connection = DriverManager
						.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
				AONContext aonContext = new AONContext(connection)) {
			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
					"grupoayudat.aonsolutions.net");
			SalaryPDFParser.parse(is, builder);
			builder.execute();
		}
//		try (InputStream is = PdfTest.class.getResourceAsStream("learning2020.pdf");
//				Connection connection = DriverManager
//						.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root")) {
//			SalaryPDFParser.parse(is, new SalaryBuilder() {
//
//				private String empName, empAddress, empCity, entDoc, entName, entAddress, ccc, cat, dni;
//				private Date senDate, startDate, endDate;
//
//				@Override
//				public void setEmployeeName(String employeeName) {
//					empName = employeeName;
//				}
//
//				@Override
//				public void setEmployeeAddress(String employeeAddress) {
//					empAddress = employeeAddress;
//				}
//
//				@Override
//				public void setEmployeeCity(String employeeCity) {
//					empCity = employeeCity;
//				}
//
//				@Override
//				public void setEnterpriseDocument(String enterpriseDocument) {
//					entDoc = enterpriseDocument;
//				}
//
//				@Override
//				public void setEnterpriseName(String enterpriseName) {
//					entName = enterpriseName;
//				}
//
//				@Override
//				public void setEnterpriseAddress(String enterpriseAddress) {
//					entAddress = enterpriseAddress;
//				}
//
//				@Override
//				public void setCcc(String ccc) {
//					this.ccc = ccc;
//				}
//
//				@Override
//				public void setCategory(String category) {
//					cat = category;
//				}
//
//				@Override
//				public void setSeniorityDate(Date seniorityDate) {
//					senDate = seniorityDate;
//				}
//
//				@Override
//				public void setEmployeeDocument(String employeeDocument) {
//					dni = employeeDocument;
//					try {
//						String sql = "select distinct UPPER(p1.name) as name, UPPER(p1.first_surname) as first, UPPER(p1.second_surname) as second from person p1, registry r1 where p1.registry=r1.id and r1.document='"
//								+ dni + "'";
//						Statement st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						while (rs.next()) {
////							assertEquals(
////									rs.getString("first") + " " + rs.getString("second") + ", " + rs.getString("name"),
////									empName);
//							System.out.println(rs.getString("first")+": "+ rs.getString("first").length());
//							System.out.println(rs.getString("second")+": "+ rs.getString("second").length());
//							System.out.println(rs.getString("name")+": "+ rs.getString("name").length());
//						}
//						sql = "select distinct enterprise_name, enterprise_address, enterprise_document, ccc, category, seniority_date from salary where employee_document = '"
//								+ dni + "'";
//						System.out.println(sql);
//						st = connection.createStatement();
//						rs = st.executeQuery(sql);
//						HashSet<String> cats = new HashSet<String>();
//						while (rs.next()) {
//							assertEquals("AYUDA-T LEARNING S.L.", rs.getString("enterprise_name"));
//							assertEquals(rs.getString("enterprise_address"), entAddress);
//							assertEquals(rs.getString("enterprise_document"), entDoc);
//							assertEquals(rs.getString("ccc"), ccc);
//							DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//							assertEquals(rs.getString("seniority_date"), df.format(senDate));
//							cats.add(rs.getString("category"));
//						}
//						if (!cats.contains(cat))
//							fail("Category fail");
//					} catch (SQLException e) {
//						fail("SQL exception");
//					}
//				}
//
//				@Override
//				public void setSocialSecurityNumber(String socialSecurityNumber) {
//					String sql = "select distinct social_security_number as nss from salary where employee_document = '"
//							+ dni + "'";
//					Statement st;
//					try {
//						st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						while (rs.next()) {
//							assertEquals(rs.getString("nss"), socialSecurityNumber);
//						}
//					} catch (SQLException e) {
//						fail("SQL exception");
//					}
//
//				}
//
//				@Override
//				public void setQuoteGroup(String quoteGroup) {
//					String sql = "select distinct quote_group as qg from salary where employee_document = '" + dni
//							+ "'";
//					Statement st;
//					try {
//						st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						while (rs.next()) {
//							assertEquals(rs.getString("qg"), quoteGroup);
//						}
//					} catch (SQLException e) {
//						fail("SQL exception");
//					}
//				}
//
//				@Override
//				public void setStartDate(Date startDate) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String dateStr = df.format(startDate);
//					this.startDate = startDate;
//					String sql = "select count(*) as c from salary where employee_document='" + dni
//							+ "' and start_date=str_to_date('" + dateStr + "','%Y-%m-%d')";
//					// String sql = "select distinct start_date as sd from salary where
//					// employee_document='"+dni+"'";
//					Statement st;
//					try {
//						st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						while (rs.next()) {
//							if (rs.getInt("c") != 1) {
//								fail("Start date fail");
//							}
//						}
//					} catch (SQLException e) {
//						fail("SQL exception");
//					}
//				}
//
//				@Override
//				public void setEndDate(Date endDate) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String dateStr = df.format(endDate);
//					this.endDate = endDate;
//					String sql = "select count(*) as c from salary where employee_document='" + dni
//							+ "' and end_date=str_to_date('" + dateStr + "','%Y-%m-%d')";
//					// String sql = "select distinct start_date as sd from salary where
//					// employee_document='"+dni+"'";
//					Statement st;
//					try {
//						st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						while (rs.next()) {
//							if (rs.getInt("c") != 1) {
//								fail("End date fail");
//							}
//						}
//					} catch (SQLException e) {
//						fail("SQL exception");
//					}
//				}
//
//				@Override
//				public void setTimeUnits(Integer timeUnits) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String sDate = df.format(startDate);
//					String eDate = df.format(endDate);
//					String sql = "select time_units as tu from salary where start_date=str_to_date('" + sDate
//							+ "','%Y-%m-%d') and end_date=str_to_date('" + eDate
//							+ "','%Y-%m-%d') and employee_document='" + dni + "'";
//					Statement st;
//					try {
//						st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						while (rs.next()) {
//							if (rs.getInt("tu") != timeUnits) {
//								fail("Time units fail");
//							}
//						}
//					} catch (SQLException e) {
//						fail("SQL exception");
//					}
//
//				}
//
//				@Override
//				public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
//						Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String sDate = df.format(startDate);
//					String eDate = df.format(endDate);
//
//					String sql = "select count(*) c from salary_payment sp where exists "
//							+ "(select id from salary where id  = sp.salary and " + "start_date=str_to_date('" + sDate
//							+ "','%Y-%m-%d') and " + "end_date=str_to_date('" + eDate + "','%Y-%m-%d') and "
//							+ "employee_document='" + dni + "') and " + "payment_concept='" + payment.getName()
//							+ "' and " + "amount=" + amount;
//
//					try {
//						Statement st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						while (rs.next()) {
//							if (rs.getInt("c") != 1) {
//								fail("payment does not exist");
//							}
//						}
//					} catch (SQLException e) {
//						fail("Payment fail");
//					}
//
//				}
//
//				@Override
//				public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
//						Map<String, ITimedVariable<?>> context) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String sDate = df.format(startDate);
//					String eDate = df.format(endDate);
//
//					String sql = "select count(*) c from salary_deduction sd where exists "
//							+ "(select id from salary where id  = sd.salary and " + "start_date=str_to_date('" + sDate
//							+ "','%Y-%m-%d') and end_date=str_to_date('" + eDate + "','%Y-%m-%d') and "
//							+ "employee_document='" + dni + "') and deduction_concept='" + deduction.getName()
//							+ "' and " + "amount=" + amount;
//					try {
//						Statement st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						while (rs.next()) {
//							if (rs.getInt("c") != 1) {
//								fail("payment does not exist");
//							}
//						}
//					} catch (SQLException e) {
//						fail("Payment sql fail");
//					}
//
//				}
//
//				@Override
//				public void setTotalPayment(Double totalPayment) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String sDate = df.format(startDate);
//					String eDate = df.format(endDate);
//
//					String sql = "select total_payment from salary where employee_document = '" + dni + "' and "
//							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
//							+ eDate + "', '%Y-%m-%d')";
//					try {
//						Statement st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						if (rs.next()) {
//							if (rs.getDouble("total_payment") != totalPayment) {
//								fail("total payment fail");
//							}
//						} else {
//							fail("total payment fail");
//						}
//					} catch (SQLException e) {
//						fail("total payment sql fail");
//					}
//
//				}
//
//				@Override
//				public void setProExtBase(Double extraPayProration) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String sDate = df.format(startDate);
//					String eDate = df.format(endDate);
//
//					String sql = "select pro_ext_base from salary where employee_document = '" + dni + "' and "
//							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
//							+ eDate + "', '%Y-%m-%d')";
//					try {
//						Statement st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						if (rs.next()) {
//							if (extraPayProration == null)
//								extraPayProration = 0.0;
//							if (rs.getDouble("pro_ext_base") != extraPayProration) {
//								fail("ProExtBase fail");
//							}
//						} else {
//							fail("ProExtBase fail");
//						}
//					} catch (SQLException e) {
//						fail("ProExtBase sql fail");
//					}
//				}
//
//				@Override
//				public void setCgcBase(Double commonBase) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String sDate = df.format(startDate);
//					String eDate = df.format(endDate);
//
//					String sql = "select cgc_base from salary where employee_document = '" + dni + "' and "
//							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
//							+ eDate + "', '%Y-%m-%d')";
//					try {
//						Statement st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						if (rs.next()) {
//							if (commonBase == null)
//								commonBase = 0.0;
//							if (rs.getDouble("cgc_base") != commonBase) {
//								fail("Cgc Base fail");
//							}
//						} else {
//							fail("Cgc Base fail");
//						}
//					} catch (SQLException e) {
//						fail("Cgc Base sql fail");
//					}
//				}
//
//				@Override
//				public void setCgpBase(Double professionalBase) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String sDate = df.format(startDate);
//					String eDate = df.format(endDate);
//
//					String sql = "select cgp_base from salary where employee_document = '" + dni + "' and "
//							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
//							+ eDate + "', '%Y-%m-%d')";
//					try {
//						Statement st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						if (rs.next()) {
//							if (professionalBase == null)
//								professionalBase = 0.0;
//							if (rs.getDouble("cgp_base") != professionalBase) {
//								fail("CgpBase fail");
//							}
//						} else {
//							fail("CgpBase fail");
//						}
//					} catch (SQLException e) {
//						fail("CgpBase sql fail");
//					}
//				}
//
//				@Override
//				public void setTotalIrpf(Double totalIrpf) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String sDate = df.format(startDate);
//					String eDate = df.format(endDate);
//
//					String sql = "select total_irpf from salary where employee_document = '" + dni + "' and "
//							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
//							+ eDate + "', '%Y-%m-%d')";
//					try {
//						Statement st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						if (rs.next()) {
//							if (totalIrpf == null)
//								totalIrpf = 0.0;
//							if (rs.getDouble("total_irpf") != totalIrpf) {
//								fail("total Irpf fail");
//							}
//						} else {
//							fail("total Irpf fail");
//						}
//					} catch (SQLException e) {
//						fail("total Irpf sql fail");
//					}
//				}
//
//				@Override
//				public void setTotalDeduction(Double totalDeduction) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String sDate = df.format(startDate);
//					String eDate = df.format(endDate);
//
//					String sql = "select total_deduction from salary where employee_document = '" + dni + "' and "
//							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
//							+ eDate + "', '%Y-%m-%d')";
//					try {
//						Statement st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						if (rs.next()) {
//							if (totalDeduction == null)
//								totalDeduction = 0.0;
//							if (rs.getDouble("total_deduction") != totalDeduction) {
//								fail("total payment fail");
//							}
//						} else {
//							fail("total deduction fail");
//						}
//					} catch (SQLException e) {
//						fail("total deduction sql fail");
//					}
//				}
//
//				@Override
//				public void setIssueDate(Date issueDate) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String sDate = df.format(startDate);
//					String eDate = df.format(endDate);
//					String iDate = df.format(issueDate);
//
//					String sql = "select issue_date from salary where employee_document = '" + dni + "' and "
//							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
//							+ eDate + "', '%Y-%m-%d')";
//					try {
//						Statement st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						if (rs.next()) {
//
//							if (!iDate.equals(rs.getString("issue_date"))) {
//								fail("Issue date fail");
//							}
//						} else {
//							fail("Issue date fail");
//						}
//					} catch (SQLException e) {
//						fail("Issue date sql fail");
//					}
//				}
//
//				@Override
//				public void setTotalLiquid(Double totalLiquid) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String sDate = df.format(startDate);
//					String eDate = df.format(endDate);
//
//					String sql = "select total_liquid from salary where employee_document = '" + dni + "' and "
//							+ "start_date = str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date = str_to_date('"
//							+ eDate + "', '%Y-%m-%d')";
//					try {
//						Statement st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						if (rs.next()) {
//							if (totalLiquid == null)
//								totalLiquid = 0.0;
//							if (rs.getDouble("total_liquid") != totalLiquid) {
//								fail("total liquid fail");
//							}
//						} else {
//							fail("total liquid fail");
//						}
//					} catch (SQLException e) {
//						fail("total payment sql fail");
//					}
//				}
//
//				@Override
//				public void addCost(Double amount, String description, Date start, Date end, IDeduction cost,
//						Map<String, ITimedVariable<?>> context) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String sDate = df.format(startDate);
//					String eDate = df.format(endDate);
//
//					String sql = "select count(*) c from salary_cost sd where exists "
//							+ "(select id from salary where id  = sd.salary and " + "start_date=str_to_date('" + sDate
//							+ "','%Y-%m-%d') and end_date=str_to_date('" + eDate + "','%Y-%m-%d') and "
//							+ "employee_document='" + dni + "') and cost_concept='" + cost.getName() + "' and "
//							+ "amount=" + amount;
//					try {
//						Statement st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						if (rs.next()) {
//							if (rs.getInt("c") != 1) {
//								fail("cost fail");
//							}
//						} else {
//							fail("cost fail");
//						}
//					} catch (SQLException e) {
//						fail("cost sql fail");
//					}
//
//				}
//
//				@Override
//				public void addData(String name, ITimedVariable<?> data) {
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//					String sDate = df.format(startDate);
//					String eDate = df.format(endDate);
//
//					String sql = "select count(*) c from salary_data sd where exists "
//							+ "(select id from salary where id  = sd.salary and " + "start_date=str_to_date('" + sDate
//							+ "','%Y-%m-%d') and end_date=str_to_date('" + eDate + "','%Y-%m-%d') and "
//							+ "employee_document='" + dni + "') and name='" + name + "' and "
//							+ "start_date=str_to_date('" + sDate + "', '%Y-%m-%d') and " + "end_date=str_to_date('"
//							+ eDate + "', '%Y-%m-%d')";
//					try {
//						Statement st = connection.createStatement();
//						ResultSet rs = st.executeQuery(sql);
//						if (rs.next()) {
//							if (rs.getInt("c") != 1) {
//								if (!(name.equals("BASE_IRPF") || name.equals("TOTAL_DEVENGADO")
//										|| name.equals("CGC_E")))
//									fail("data fail");
//
//								// CONCEPTS IGNORED ON THE IF CLAUSE ARE IGNORED WHEN INSERTING
//
//							}
//						} else {
//							fail("data fail");
//						}
//					} catch (SQLException e) {
//						fail("data sql fail");
//					}
//				}
//
//// 'contract', 'person', 'enterprise', 'salary' , 'salary_payment' y
//// 'salary_deduction'
//			});
//
//		}
	}
	@Test
	//@Ignore
	public void testA3_344() throws IOException, UnknownPDFException, SQLException {
		try (InputStream is = PdfTest.class.getResourceAsStream("NOMINAS UN LUGAR 2020.pdf");
				Connection connection = DriverManager
						.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
				AONContext aonContext = new AONContext(connection)) {
			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
					"ayudat.aonsolutions.net");
			SalaryPDFParser.parse(is, builder);
			builder.execute();
		}
	}
	@Test
	//@Ignore
	public void testA3_260() throws IOException, UnknownPDFException, SQLException {
		try (InputStream is = PdfTest.class.getResourceAsStream("nomina260.pdf");
				Connection connection = DriverManager
						.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
				AONContext aonContext = new AONContext(connection)) {
			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
					"ayudat.aonsolutions.net");
			SalaryPDFParser.parse(is, builder);
			builder.execute();
		}
	}
	
	@Test
	//@Ignore
	public void testA3_Massive() throws IOException, UnknownPDFException, SQLException {
		try (InputStream is = PdfTest.class.getResourceAsStream("NOMINAS ATSP 2020.pdf");
				Connection connection = DriverManager
						.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
				AONContext aonContext = new AONContext(connection)) {
			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
					"ayudat.aonsolutions.net");
			SalaryPDFParser.parse(is, builder);
			builder.execute();
		}
	}
	
	@Test
	//@Ignore
	public void testA3_MassiveOnlyPage1() throws IOException, UnknownPDFException, SQLException {
		try (InputStream is = PdfTest.class.getResourceAsStream("pagina1.pdf");
				Connection connection = DriverManager
						.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
				AONContext aonContext = new AONContext(connection)) {
			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
					"ayudat.aonsolutions.net");
			SalaryPDFParser.parse(is, builder);
			builder.execute();
		}
	}
	
	
	@Test
	//@Ignore
	public void testAplifisaMassive() throws IOException, UnknownPDFException, SQLException {
		try (InputStream is = PdfTest.class.getResourceAsStream("aplifisa/nominas 2020.pdf");
				Connection connection = DriverManager
						.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
				AONContext aonContext = new AONContext(connection)) {
			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
					"ayudat.aonsolutions.net");
			SalaryPDFParser.parse(is, builder);
			builder.execute();
		}
	}
	
	@Test
	//@Ignore
	public void testAplifisa584() throws IOException, UnknownPDFException, SQLException {
		try (InputStream is = PdfTest.class.getResourceAsStream("aplifisa/nomina584.pdf");
				Connection connection = DriverManager
						.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
				AONContext aonContext = new AONContext(connection)) {
			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
					"ayudat.aonsolutions.net");
			SalaryPDFParser.parse(is, builder);
			builder.execute();
		}
	}
	
	@Test
	//@Ignore
	public void testAplifisa1424() throws IOException, UnknownPDFException, SQLException {
		try (InputStream is = PdfTest.class.getResourceAsStream("aplifisa/1424.pdf");
				Connection connection = DriverManager
						.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
				AONContext aonContext = new AONContext(connection)) {
			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
					"ayudat.aonsolutions.net");
			SalaryPDFParser.parse(is, builder);
			builder.execute();
		}
	}
	
	@Test
	//@Ignore
	public void testAplifisaIRPF() throws IOException, UnknownPDFException, SQLException {
		try (InputStream is = PdfTest.class.getResourceAsStream("aplifisa/nomina_testeo.pdf");
				Connection connection = DriverManager
						.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
				AONContext aonContext = new AONContext(connection)) {
			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
					"ayudat.aonsolutions.net");
			SalaryPDFParser.parse(is, builder);
			builder.execute();
		}
	}
}
