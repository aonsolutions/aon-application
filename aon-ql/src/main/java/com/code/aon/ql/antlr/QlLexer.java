// $ANTLR : "ql.g" -> "QlLexer.java"$

package com.code.aon.ql.antlr;

import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;

import antlr.ANTLRHashString;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;
import antlr.NoViableAltForCharException;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.collections.impl.BitSet;

/**
 * ANTLR generated class.
 * 
 * @author Consulting & Development. ecastellano - 11/10/2006
 *
 */
public class QlLexer extends antlr.CharScanner implements QLTokenTypes,
		TokenStream {
	/**
	 * @param in
	 */
	public QlLexer(InputStream in) {
		this(new ByteBuffer(in));
	}

	/**
	 * @param in
	 */
	public QlLexer(Reader in) {
		this(new CharBuffer(in));
	}

	/**
	 * @param ib
	 */
	public QlLexer(InputBuffer ib) {
		this(new LexerSharedInputState(ib));
	}

	/**
	 * @param state
	 */
	@SuppressWarnings("unchecked")
	public QlLexer(LexerSharedInputState state) {
		super(state);
		caseSensitiveLiterals = true;
		setCaseSensitive(true);
		literals = new Hashtable();
		literals.put(new ANTLRHashString("null", this), new Integer(18));
		literals.put(new ANTLRHashString("not null", this), new Integer(19));
	}

	public Token nextToken() throws TokenStreamException {
		tryAgain: for (;;) {
			int _ttype = Token.INVALID_TYPE;
			resetText();
			try { // for char stream error handling
				try { // for lexical error handling
					switch (LA(1)) {
					case '&': {
						mAND(true);
						break;
					}
					case '|': {
						mOR(true);
						break;
					}
					case ':': {
						mDOTS(true);
						break;
					}
					case '(': {
						mLPAREN(true);
						break;
					}
					case ')': {
						mRPAREN(true);
						break;
					}
					case '[': {
						mLBRACKET(true);
						break;
					}
					case ']': {
						mRBRACKET(true);
						break;
					}
					case '=': {
						mEQ(true);
						break;
					}
					case '?': {
						mQUESTION(true);
						break;
					}
					case '*': {
						mASTERISC(true);
						break;
					}
					case '\'': {
						mWS(true);
						break;
					}
					default:
						if ((LA(1) == '<') && (LA(2) == '=')) {
							mLTE(true);
						} else if ((LA(1) == '>') && (LA(2) == '=')) {
							mGTE(true);
						} else if ((LA(1) == '!') && (LA(2) == '=')) {
							mNOTEQ(true);
						} else if ((LA(1) == '>') && (LA(2) == '>')) {
							mSR(true);
						} else if ((LA(1) == '<') && (LA(2) == '<')) {
							mSL(true);
						} else if ((LA(1) == '!') && (true)) {
							mNOT(true);
						} else if ((LA(1) == '<') && (true)) {
							mLT(true);
						} else if ((LA(1) == '>') && (true)) {
							mGT(true);
						} else if ((_tokenSet_0.member(LA(1)))) {
							mLITERAL(true);
						} else {
							if (LA(1) == EOF_CHAR) {
								uponEOF();
								_returnToken = makeToken(Token.EOF_TYPE);
							} else {
								throw new NoViableAltForCharException(LA(1),
										getFilename(), getLine(), getColumn());
							}
						}
					}
					if (_returnToken == null)
						continue tryAgain; // found SKIP token
					_ttype = _returnToken.getType();
					_ttype = testLiteralsTable(_ttype);
					_returnToken.setType(_ttype);
					return _returnToken;
				} catch (RecognitionException e) {
					throw new TokenStreamRecognitionException(e);
				}
			} catch (CharStreamException cse) {
				if (cse instanceof CharStreamIOException) {
					throw new TokenStreamIOException(
							((CharStreamIOException) cse).io);
				}
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mNOT(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = NOT;
		match('!');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mAND(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = AND;
		match('&');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mOR(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = OR;
		match('|');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mDOTS(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = DOTS;
		match(':');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mLPAREN(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LPAREN;
		match('(');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mRPAREN(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = RPAREN;
		match(')');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mLBRACKET(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LBRACKET;
		match('[');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mRBRACKET(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = RBRACKET;
		match(']');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mEQ(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = EQ;
		match('=');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mLT(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LT;
		match('<');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mGT(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = GT;
		match('>');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mLTE(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LTE;
		match("<=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mGTE(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = GTE;
		match(">=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mNOTEQ(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = NOTEQ;
		match("!=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mSR(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = SR;
		match(">>");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mSL(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = SL;
		match("<<");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mQUESTION(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = QUESTION;
		match('?');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mASTERISC(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = ASTERISC;
		match('*');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mLITERAL(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LITERAL;
		{
			int _cnt114 = 0;
			_loop114: do {
				if ((LA(1) == '\\')) {
					mESC(false);
				} else if ((_tokenSet_1.member(LA(1)))) {
					{
						match(_tokenSet_1);
					}
				} else {
					if (_cnt114 >= 1) {
						break _loop114;
					}
					throw new NoViableAltForCharException(LA(1),
							getFilename(), getLine(), getColumn());
				}

				_cnt114++;
			} while (true);
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	protected final void mESC(boolean _createToken)
			throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = ESC;
		int _saveIndex;

		_saveIndex = text.length();
		match('\\');
		text.setLength(_saveIndex);
		{
			switch (LA(1)) {
			case '>': {
				match('>');
				break;
			}
			case '<': {
				match('<');
				break;
			}
			case '=': {
				match('=');
				break;
			}
			case '|': {
				match('|');
				break;
			}
			case '&': {
				match('&');
				break;
			}
			case '!': {
				match('!');
				break;
			}
			case '*': {
				match('*');
				break;
			}
			case '?': {
				match('?');
				break;
			}
			case '(': {
				match('(');
				break;
			}
			case ')': {
				match(')');
				break;
			}
			case '[': {
				match('[');
				break;
			}
			case ']': {
				match(']');
				break;
			}
			case ':': {
				match(':');
				break;
			}
			case '\\': {
				match('\\');
				break;
			}
			case '\'': {
				match('\'');
				break;
			}
			case '"': {
				match('"');
				break;
			}
			default: {
				throw new NoViableAltForCharException(LA(1),
						getFilename(), getLine(), getColumn());
			}
			}
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	/**
	 * @param _createToken
	 * @throws RecognitionException
	 * @throws CharStreamException
	 * @throws TokenStreamException
	 */
	public final void mWS(boolean _createToken) throws RecognitionException,
			CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = WS;
		int _saveIndex;

		{
			_saveIndex = text.length();
			match('\'');
			text.setLength(_saveIndex);
		}
		{
			_loop118: do {
				switch (LA(1)) {
				case ' ': {
					match(' ');
					break;
				}
				case '\t': {
					match('\t');
					break;
				}
				case '\u000c': {
					match('\f');
					break;
				}
				default: {
					break _loop118;
				}
				}
			} while (true);
		}
		{
			_saveIndex = text.length();
			match('\'');
			text.setLength(_saveIndex);
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()
					- _begin));
		}
		_returnToken = _token;
	}

	private static final long[] mk_tokenSet_0() {
		long[] data = new long[260];
		data[0] = 864682581470216184L;
		data[1] = -1152921505277935617L;
		for (int i = 2; i <= 3; i++) {
			data[i] = -1L;
		}
		for (int i = 64; i <= 127; i++) {
			data[i] = -1L;
		}
		return data;
	}

	/**
	 * 
	 */
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());

	private static final long[] mk_tokenSet_1() {
		long[] data = new long[260];
		data[0] = 864682581470216184L;
		data[1] = -1152921505546371073L;
		for (int i = 2; i <= 3; i++) {
			data[i] = -1L;
		}
		for (int i = 64; i <= 127; i++) {
			data[i] = -1L;
		}
		return data;
	}

	/**
	 * 
	 */
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());

}
