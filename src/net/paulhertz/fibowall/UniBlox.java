package net.paulhertz.fibowall;

import java.util.Hashtable;

/**
 * Load production strings into transTable with put() command.
 * Load final output strings into codeTable with encode() command.
 * Generally, every key entered into transTable should also appear in codeTable, but
 * you could also write or skip character that don't appear in the codeTable. 
 */
public class UniBlox extends Object {
	
  // UniCode blocks encoded in HTML
// TODO refactor: use long names in Blocks and Geometric sections
  public static final String U12 = "&#x2580;";
  public static final String L18 = "&#x2581;";
  public static final String L14 = "&#x2582;";
  public static final String L38 = "&#x2583;";
  public static final String L12 = "&#x2584;";
  public static final String L58 = "&#x2585;";
  public static final String L34 = "&#x2586;";
  public static final String L78 = "&#x2587;";
  public static final String FB  = "&#x2588;";
  public static final String Z78 = "&#x2589;";
  public static final String Z34 = "&#x258A;";
  public static final String Z58 = "&#x258B;";
  public static final String Z12 = "&#x258C;";
  public static final String Z38 = "&#x258D;";
  public static final String Z14 = "&#x258E;";
  public static final String Z18 = "&#x258F;";
  public static final String R12 = "&#x2590;";
  public static final String LIGHT  = "&#x2591;";
  public static final String MEDIUM = "&#x2592;";
  public static final String DARK   = "&#x2593;";
  public static final String U18 = "&#x2594;";
  public static final String R18 = "&#x2595;";  
  // UniCode squares encoded in HTML
  public static final String blacksquare = "&#x25A0;";
  public static final String whitesquare = "&#x25A1;";
  public static final String whitewithblack = "&#x25A3;";
  public static final String hzfill = "&#x25A4;";
  public static final String vtfill = "&#x25A5;";
  public static final String hatch = "&#x25A6;";
  public static final String ullr = "&#x25A7;";
  public static final String urll = "&#x25A8;";
  public static final String diaghatch = "&#x25A9;";
  public static final String smallblack = "&#x25AA;";
  public static final String smallwhite = "&#x25AB;";
  
  // UniCode combining
  //  Chars staying in the middle
  //---------------------------
  public static final String comCommaAboveRight = "&#789;";
  public static final String comHorm = "&#795;";
  public static final String comGraveToneMark = "&#832;";
  public static final String comAcuteToneMark = "&#833;";
  public static final String comDotAboveRight = "&#856;";
  public static final String comPalatizedHookBelow = "&#801;";
  public static final String comReroflexHookBelow = "&#802;";
  public static final String comCedilla = "&#807;";
  public static final String comOgonek = "&#808;";
  public static final String comTildeOverlay = "&#820;";
  public static final String comShortStrokeOverlay = "&#821;";
  public static final String comLongStrokeOverlay = "&#822;";
  public static final String comGraphemeJoiner = "&#847;";
  public static final String comDoubleBreveBelow = "&#860;";
  public static final String comDoubleBreve = "&#861;";
  public static final String comDoubleMacron = "&#862;";
  public static final String comDoubleMacronBelow = "&#863;";
  public static final String comDoubleTilde = "&#864;";
  public static final String comDoubleRightwardsArrowBelow = "&#866;";
  public static final String comLongSolidusOverlay = "&#824;";
  public static final String comShortSolidusOverlay = "&#823;";
  public static final String comDoubleInvertedBreve = "&#865;";
  public static final String comCyrillicMillionsSign = "&#1161;";
  public static final String comEnclosingCircle = "&#8413;";
  public static final String comEnclosingSquare = "&#8414;";
  public static final String comEnclosingDiamond = "&#8415;";
  public static final String comEnclosingCircleBackslash = "&#8416;";
  // chars going up
  // --------------
  public static final String comVerticalLineAbove = "&#781;";
  public static final String comDoubleVerticalLineAbove = "&#782;";
  public static final String comMacron = "&#772;";
  public static final String comOverline = "&#773;";
  public static final String comDoubleOverline = "&#831;";
  public static final String comInvertedBreve = "&#785;";
  public static final String comBreve = "&#774;";
  public static final String comCandrabindu = "&#784;";
  public static final String comFermata = "&#850;";
  public static final String comRightHalfRingAbove = "&#855;";
  public static final String comLeftHalfRingAbove = "&#849;";
  public static final String comDotAbove = "&#775;";
  public static final String comDiaresis = "&#776;";
  public static final String comRingAbove = "&#778;";
  public static final String comGreekPerispomeni = "&#834;";
  public static final String comGreekKoronis = "&#835;";
  public static final String comGreekDialytikaTonos = "&#836;";
  public static final String comNotTildeAbove = "&#842;";
  public static final String comHomotheticAbove = "&#843;";
  public static final String comAlmostEqualToAbove = "&#844;";
  public static final String comTilde = "&#771;";
  public static final String comCircumflexAccent = "&#770;";
  public static final String comCaron = "&#780;";
  public static final String comRightArrowheadAbove = "&#848;";
  public static final String comGraveAccent = "&#768;";
  public static final String comAcuteAccent = "&#769;";
  public static final String comDoubleAcuteAccent = "&#779;";
  public static final String comDoubleGraveAccent = "&#783;";
  public static final String comTurnedCommaAbove = "&#786;";
  public static final String comCommaAbove = "&#787;";
  public static final String comReversedCommaAbove = "&#788;";
  public static final String comXAbove = "&#829;";
  public static final String comHookAbove = "&#777;";
  public static final String comLatinSmallLetterA = "&#867;";
  public static final String comLatinSmallLetterE = "&#868;";
  public static final String comLatinSmallLetterI = "&#869;";
  public static final String comLatinSmallLetterO = "&#870;";
  public static final String comLatinSmallLetterU = "&#871;";
  public static final String comLatinSmallLetterC = "&#872;";
  public static final String comLatinSmallLetterD = "&#873;";
  public static final String comLatinSmallLetterH = "&#874;";
  public static final String comLatinSmallLetterM = "&#875;";
  public static final String comLatinSmallLetterR = "&#876;";
  public static final String comLatinSmallLetterT = "&#877;";
  public static final String comLatinSmallLetterV = "&#878;";
  public static final String comLatinSmallLetterX = "&#879;";
  public static final String comVerticalTilde = "&#830;";
  public static final String comZigzagAbove = "&#859;";
  public static final String comBridgeAbove = "&#838;";
  public static final String comLeftAngleAbove = "&#794;";
  // Chars going down
  // ----------------
  public static final String comGraveAccentBelow = "&#790;";
  public static final String comAccuteAccentBelow = "&#791;";
  public static final String comLeftTackBelow = "&#792;";
  public static final String comRightTackBelow = "&#793;";
  public static final String comLeftHalfRingBelow = "&#796;";
  public static final String comUpTackBelow = "&#797;";
  public static final String comDownTackBelow = "&#798;";
  public static final String comPlusSignBelow = "&#799;";
  public static final String comMinusSignBelow = "&#800;";
  public static final String comDiaeresisBelow = "&#804;";
  public static final String comRingBelow = "&#805;";
  public static final String comCommaBelow = "&#806;";
  public static final String comVerticalLineBelow = "&#809;";
  public static final String comBridgeBelow = "&#810;";
  public static final String comInvertedDoubleArchBelow = "&#811;";
  public static final String comCaronBelow = "&#812;";
  public static final String comCircumflexAccentBelow = "&#813;";
  public static final String comBreveBelow = "&#814;";
  public static final String comInvertedBreveBelow = "&#815;";
  public static final String comTildeBelwow = "&#816;";
  public static final String comMacronBelow = "&#817;";
  public static final String comLowLine = "&#818;";
  public static final String comDoubleLowLine = "&#819;";
  public static final String comRightHalfRingBelow = "&#825;";
  public static final String comInvertedBridgeBelow = "&#826;";
  public static final String comSquareBelow = "&#827;";
  public static final String comSeagullBelow = "&#828;";
  public static final String comGreekYpogegrammeni = "&#837;";
  public static final String comEqualsSignBelow = "&#839;";
  public static final String comDoubleVerticalBelow = "&#840;";
  public static final String comLeftAngleBelow = "&#841;";
  public static final String comLeftRightArrowBelow = "&#845;";
  public static final String comUpwardsArrowBelow = "&#846;";
  public static final String comXBelow = "&#851;";
  public static final String comLeftArrowheadBelow = "&#852;";
  public static final String comRightArrowheadBelow = "&#853;";
  public static final String comRightArrowheadAndUpArrowheadBelow = "&#854;";
  public static final String comAsteriskBelow = "&#857;";
  public static final String comDoubleRingBelow = "&#858;";
  public static final String comDotBelow = "&#803;";
  // International Phonetic Alphabet extensions
  public static final String  turned_a = "&#x0250;";
  public static final String  alpha = "&#x0251;";
  public static final String  turned_alpha = "&#x0252;";
  public static final String  b_with_hook = "&#x0253;";
  public static final String  open_o = "&#x0254;";
  public static final String  c_with_curl = "&#x0255;";
  public static final String  d_with_tail = "&#x0256;";
  public static final String  d_with_hook = "&#x0257;";
  public static final String  reversed_e = "&#x0258;";
  public static final String  schwa = "&#x0259;";
  public static final String  schwa_with_hook = "&#x025A;";
  public static final String  open_e = "&#x025B;";
  public static final String  reversed_open_e = "&#x025C;";
  public static final String  reversed_open_e_with_hook = "&#x025D;";
  public static final String  closed_reversed_open_e = "&#x025E;";
  public static final String  dotless_j_with_stroke = "&#x025F;";
  public static final String  g_with_hook = "&#x0260;";
  public static final String  script_g = "&#x0261;";
  public static final String  capital_g = "&#x0262;";
  public static final String  gamma = "&#x0263;";
  public static final String  rams_horn = "&#x0264;";
  public static final String  turned_h = "&#x0265;";
  public static final String  h_with_hook = "&#x0266;";
  public static final String  heng_with_hook = "&#x0267;";
  public static final String  i_with_stroke = "&#x0268;";
  public static final String  iota = "&#x0269;";
  public static final String  capital_i = "&#x026A;";
  public static final String  l_with_middle_tilde = "&#x026B;";
  public static final String  l_with_belt = "&#x026C;";
  public static final String  l_with_retroflex_hook = "&#x026D;";
  public static final String  lezh = "&#x026E;";
  public static final String  turned_m = "&#x026F;";
  public static final String  turned_m_with_long_leg = "&#x0270;";
  public static final String  m_with_hook = "&#x0271;";
  public static final String  n_with_left_hook = "&#x0272;";
  public static final String  n_with_retroflex_hook = "&#x0273;";
  public static final String  capital_n = "&#x0274;";
  public static final String  barred_o = "&#x0275;";
  public static final String  capital_oe = "&#x0276;";
  public static final String  closed_omega = "&#x0277;";
  public static final String  phi = "&#x0278;";
  public static final String  turned_r = "&#x0279;";
  public static final String  turned_r_with_long_leg = "&#x027A;";
  public static final String  turned_r_with_hook = "&#x027B;";
  public static final String  r_with_long_leg = "&#x027C;";
  public static final String  r_with_tail = "&#x027D;";
  public static final String  r_with_fishhook = "&#x027E;";
  public static final String  reversed_r_with_fishhook = "&#x027F;";
  public static final String  capital_r = "&#x0280;";
  public static final String  capital_inverted_r = "&#x0281;";
  public static final String  s_with_hook = "&#x0282;";
  public static final String  esh = "&#x0283;";
  public static final String  dotless_j_with_stroke_and_hook = "&#x0284;";
  public static final String  squat_reversed_esh = "&#x0285;";
  public static final String  esh_with_curl = "&#x0286;";
  public static final String  turned_t = "&#x0287;";
  public static final String  t_with_retroflex_hook = "&#x0288;";
  public static final String  u_bar = "&#x0289;";
  public static final String  upsilon = "&#x028A;";
  public static final String  v_with_hook = "&#x028B;";
  public static final String  turned_v = "&#x028C;";
  public static final String  turned_w = "&#x028D;";
  public static final String  turned_y = "&#x028E;";
  public static final String  capital_y = "&#x028F;";
  public static final String  z_with_retroflex_hook = "&#x0290;";
  public static final String  z_with_curl = "&#x0291;";
  public static final String  ezh = "&#x0292;";
  public static final String  ezh_with_curl = "&#x0293;";
  public static final String  glottal_stop = "&#x0294;";
  public static final String  pharyngeal_voiced_fricative = "&#x0295;";
  public static final String  inverted_glottal_stop = "&#x0296;";
  public static final String  stretched_c = "&#x0297;";
  public static final String  bilabial_click = "&#x0298;";
  public static final String  small_capital_b = "&#x0299;";
  public static final String  closed_open_e = "&#x029A;";
  public static final String  small_capital_g_with_hook = "&#x029B;";
  public static final String  small_capital_h = "&#x029C;";
  public static final String  j_with_crossed_tail = "&#x029D;";
  public static final String  turned_k = "&#x029E;";
  public static final String  small_capital_l = "&#x029F;";
  public static final String  q_with_hook = "&#x02A0;";
  public static final String  glottal_stop_with_stroke = "&#x02A1;";
  public static final String  reversed_glottal_stop_with_stroke = "&#x02A2;";
  public static final String  dz_digraph = "&#x02A3;";
  public static final String  dezh_digraph = "&#x02A4;";
  public static final String  dz_digraph_with_curl = "&#x02A5;";
  public static final String  ts_digraph = "&#x02A6;";
  public static final String  tesh_digraph = "&#x02A7;";
  public static final String  tc_digraph_with_curl = "&#x02A8;";
  public static final String  feng_digraph = "&#x02A9;";
  public static final String  ls_digraph = "&#x02AA;";
  public static final String  lz_digraph = "&#x02AB;";
  public static final String  bilabial_percussive = "&#x02AC;";
  public static final String  bidental_percussive = "&#x02AD;";
  
  // box drawings
  public static final String boxLightHorizontal = "&#9472;";	// ─
  public static final String boxHeavyHorizontal = "&#9473;";	// ━
  public static final String boxLightVertical = "&#9474;";	// │
  public static final String boxHeavyVertical = "&#9475;";	// ┃
  public static final String boxLightTripleDashHorizontal = "&#9476;";	// ┄
  public static final String boxHeavyTripleDashHorizontal = "&#9477;";	// ┅
  public static final String boxLightTripleDashVertical = "&#9478;";	// ┆
  public static final String boxHeavyTripleDashVertical = "&#9479;";	// ┇
  public static final String boxLightQuadrupleDashHorizontal = "&#9480;";	// ┈
  public static final String boxHeavyQuadrupleDashHorizontal = "&#9481;";	// ┉
  public static final String boxLightQuadrupleDashVertical = "&#9482;";	// ┊
  public static final String boxHeavyQuadrupleDashVertical = "&#9483;";	// ┋
  public static final String boxLightDownAndRight = "&#9484;";	// ┌
  public static final String boxDownLightAndRightHeavy = "&#9485;";	// ┍
  public static final String boxDownHeavyAndRightLight = "&#9486;";	// ┎
  public static final String boxHeavyDownAndRight = "&#9487;";	// ┏
  public static final String boxLightDownAndLeft = "&#9488;";	// ┐
  public static final String boxDownLightAndLeftHeavy = "&#9489;";	// ┑
  public static final String boxDownHeavyAndLeftLight = "&#9490;";	// ┒
  public static final String boxHeavyDownAndLeft = "&#9491;";	// ┓
  public static final String boxLightUpAndRight = "&#9492;";	// └
  public static final String boxUpLightAndRightHeavy = "&#9493;";	// ┕
  public static final String boxUpHeavyAndRightLight = "&#9494;";	// ┖
  public static final String boxHeavyUpAndRight = "&#9495;";	// ┗
  public static final String boxLightUpAndLeft = "&#9496;";	// ┘
  public static final String boxUpLightAndLeftHeavy = "&#9497;";	// ┙
  public static final String boxUpHeavyAndLeftLight = "&#9498;";	// ┚
  public static final String boxHeavyUpAndLeft = "&#9499;";	// ┛
  public static final String boxLightVerticalAndRight = "&#9500;";	// ├
  public static final String boxVerticalLightAndRightHeavy = "&#9501;";	// ┝
  public static final String boxUpHeavyAndRightDownLight  = "&#9502;";	// ┞
  public static final String boxDownHeavyAndRightUpLight  = "&#9503;";	// ┟
  public static final String boxVerticalHeavyAndRightLight = "&#9504;";	// ┠
  public static final String boxDownLightAndRightUpHeavy  = "&#9505;";	// ┡
  public static final String boxUpLightAndRightDownHeavy  = "&#9506;";	// ┢
  public static final String boxHeavyVerticalAndRight = "&#9507;";	// ┣
  public static final String boxLightVerticalAndLeft = "&#9508;";	// ┤
  public static final String boxVerticalLightAndLeftHeavy = "&#9509;";	// ┥
  public static final String boxUpHeavyAndLeftDownLight  = "&#9510;";	// ┦
  public static final String boxDownHeavyAndLeftUpLight  = "&#9511;";	// ┧
  public static final String boxVerticalHeavyAndLeftLight = "&#9512;";	// ┨
  public static final String boxDownLightAndLeftUpHeavy  = "&#9513;";	// ┩
  public static final String boxUpLightAndLeftDownHeavy  = "&#9514;";	// ┪
  public static final String boxHeavyVerticalAndLeft = "&#9515;";	// ┫
  public static final String boxLightDownAndHorizontal = "&#9516;";	// ┬
  public static final String boxLeftHeavyAndRightDownLight  = "&#9517;";	// ┭
  public static final String boxRightHeavyAndLeftDownLight  = "&#9518;";	// ┮
  public static final String boxDownLightAndHorizontalHeavy = "&#9519;";	// ┯
  public static final String boxDownHeavyAndHorizontalLight = "&#9520;";	// ┰
  public static final String boxRightLightAndLeftDownHeavy  = "&#9521;";	// ┱
  public static final String boxLeftLightAndRightDownHeavy  = "&#9522;";	// ┲
  public static final String boxHeavyDownAndHorizontal = "&#9523;";	// ┳
  public static final String boxLightUpAndHorizontal = "&#9524;";	// ┴
  public static final String boxLeftHeavyAndRightUpLight  = "&#9525;";	// ┵
  public static final String boxRightHeavyAndLeftUpLight  = "&#9526;";	// ┶
  public static final String boxUpLightAndHorizontalHeavy = "&#9527;";	// ┷
  public static final String boxUpHeavyAndHorizontalLight = "&#9528;";	// ┸
  public static final String boxRightLightAndLeftUpHeavy  = "&#9529;";	// ┹
  public static final String boxLeftLightAndRightUpHeavy  = "&#9530;";	// ┺
  public static final String boxHeavyUpAndHorizontal = "&#9531;";	// ┻
  public static final String boxLightVerticalAndHorizontal = "&#9532;";	// ┼
  public static final String boxLeftHeavyAndRightVerticalLight  = "&#9533;";	// ┽
  public static final String boxRightHeavyAndLeftVerticalLight  = "&#9534;";	// ┾
  public static final String boxVerticalLightAndHorizontalHeavy = "&#9535;";	// ┿
  public static final String boxUpHeavyAndDownHorizontalLight  = "&#9536;";	// ╀
  public static final String boxDownHeavyAndUpHorizontalLight  = "&#9537;";	// ╁
  public static final String boxVerticalHeavyAndHorizontalLight = "&#9538;";	// ╂
  public static final String boxLeftUpHeavyAndRightDownLight = "&#9539;";	// ╃
  public static final String boxRightUpHeavyAndLeftDownLight = "&#9540;";	// ╄
  public static final String boxLeftDownHeavyAndRightUpLight = "&#9541;";	// ╅
  public static final String boxRightDownHeavyAndLeftUpLight = "&#9542;";	// ╆
  public static final String boxDownLightAndUpHorizontalHeavy = "&#9543;";	// ╇
  public static final String boxUpLightAndDownHorizontalHeavy = "&#9544;";	// ╈
  public static final String boxRightLightAndLeftVerticalHeavy = "&#9545;";	// ╉
  public static final String boxLeftLightAndRightVerticalHeavy = "&#9546;";	// ╊
  public static final String boxHeavyVerticalAndHorizontal = "&#9547;";	// ╋
  public static final String boxLightDoubleDashHorizontal = "&#9548;";	// ╌
  public static final String boxHeavyDoubleDashHorizontal = "&#9549;";	// ╍
  public static final String boxLightDoubleDashVertical = "&#9550;";	// ╎
  public static final String boxHeavyDoubleDashVertical = "&#9551;";	// ╏
  public static final String boxDoubleHorizontal = "&#9552;";	// ═
  public static final String boxDoubleVertical = "&#9553;";	// ║
  public static final String boxDownSingleAndRightDouble = "&#9554;";	// ╒
  public static final String boxDownDoubleAndRightSingle = "&#9555;";	// ╓
  public static final String boxDoubleDownAndRight = "&#9556;";	// ╔
  public static final String boxDownSingleAndLeftDouble = "&#9557;";	// ╕
  public static final String boxDownDoubleAndLeftSingle = "&#9558;";	// ╖
  public static final String boxDoubleDownAndLeft = "&#9559;";	// ╗
  public static final String boxUpSingleAndRightDouble = "&#9560;";	// ╘
  public static final String boxUpDoubleAndRightSingle = "&#9561;";	// ╙
  public static final String boxDoubleUpAndRight = "&#9562;";	// ╚
  public static final String boxUpSingleAndLeftDouble = "&#9563;";	// ╛
  public static final String boxUpDoubleAndLeftSingle = "&#9564;";	// ╜
  public static final String boxDoubleUpAndLeft = "&#9565;";	// ╝
  public static final String boxVerticalSingleAndRightDouble = "&#9566;";	// ╞
  public static final String boxVerticalDoubleAndRightSingle = "&#9567;";	// ╟
  public static final String boxDoubleVerticalAndRight = "&#9568;";	// ╠
  public static final String boxVerticalSingleAndLeftDouble = "&#9569;";	// ╡
  public static final String boxVerticalDoubleAndLeftSingle = "&#9570;";	// ╢
  public static final String boxDoubleVerticalAndLeft = "&#9571;";	// ╣
  public static final String boxDownSingleAndHorizontalDouble = "&#9572;";	// ╤
  public static final String boxDownDoubleAndHorizontalSingle = "&#9573;";	// ╥
  public static final String boxDoubleDownAndHorizontal = "&#9574;";	// ╦
  public static final String boxUpSingleAndHorizontalDouble = "&#9575;";	// ╧
  public static final String boxUpDoubleAndHorizontalSingle = "&#9576;";	// ╨
  public static final String boxDoubleUpAndHorizontal = "&#9577;";	// ╩
  public static final String boxVerticalSingleAndHorizontalDouble = "&#9578;";	// ╪
  public static final String boxVerticalDoubleAndHorizontalSingle = "&#9579;";	// ╫
  public static final String boxDoubleVerticalAndHorizontal = "&#9580;";	// ╬
  public static final String boxLightArcDownAndRight = "&#9581;";	// ╭
  public static final String boxLightArcDownAndLeft = "&#9582;";	// ╮
  public static final String boxLightArcUpAndLeft = "&#9583;";	// ╯
  public static final String boxLightArcUpAndRight = "&#9584;";	// ╰
  public static final String boxLightDiagonalUpperRightToLowerLeft = "&#9585;";	// ╱
  public static final String boxLightDiagonalUpperLeftToLowerRight = "&#9586;";	// ╲
  public static final String boxLightDiagonalCross = "&#9587;";	// ╳
  public static final String boxLightLeft = "&#9588;";	// ╴
  public static final String boxLightUp = "&#9589;";	// ╵
  public static final String boxLightRight = "&#9590;";	// ╶
  public static final String boxLightDown = "&#9591;";	// ╷
  public static final String boxHeavyLeft = "&#9592;";	// ╸
  public static final String boxHeavyUp = "&#9593;";	// ╹
  public static final String boxHeavyRight = "&#9594;";	// ╺
  public static final String boxHeavyDown = "&#9595;";	// ╻
  public static final String boxLightLeftAndHeavyRight = "&#9596;";	// ╼
  public static final String boxLightUpAndHeavyDown = "&#9597;";	// ╽
  public static final String boxHeavyLeftAndLightRight = "&#9598;";	// ╾
  public static final String boxHeavyUpAndLightDown = "&#9599;";	// ╿
  
 // Blocks 

  public static final String upperHalfBlock = "&#x2580;";
  public static final String lowerOneEighthBlock = "&#x2581;";
  public static final String lowerOneQuarterBlock = "&#x2582;";
  public static final String lowerThreeEighthsBlock = "&#x2583;";
  public static final String lowerHalfBlock = "&#x2584;";
  public static final String lowerFiveEighthsBlock = "&#x2585;";
  public static final String lowerThreeQuartersBlock = "&#x2586;";
  public static final String lowerSevenEighthsBlock = "&#x2587;";
  public static final String fullBlock = "&#x2588;";
  public static final String leftSevenEighthsBlock = "&#x2589;";
  public static final String leftThreeQuartersBlock = "&#x258a;";
  public static final String leftFiveEighthsBlock = "&#x258b;";
  public static final String leftHalfBlock = "&#x258c;";
  public static final String leftThreeEighthsBlock = "&#x258d;";
  public static final String leftOneQuarterBlock = "&#x258e;";
  public static final String leftOneEighthBlock = "&#x258f;";
  public static final String rightHalfBlock = "&#x2590;";
  public static final String lightShade = "&#x2591;";
  public static final String mediumShade = "&#x2592;";
  public static final String darkShade = "&#x2593;";
  public static final String upperOneEighthBlock = "&#x2594;";
  public static final String rightOneEighthBlock = "&#x2595;";
  
  // Geometric
  
  public static final String blackSquare = "&#x25a0;";
  public static final String whiteSquare = "&#x25a1;";
  public static final String whiteSquareWithRoundedCorners = "&#x25a2;";
  public static final String whiteSquareContainingBlackSmallSquare = "&#x25a3;";
  public static final String squareWithHorizontalFill = "&#x25a4;";
  public static final String squareWithVerticalFill = "&#x25a5;";
  public static final String squareWithOrthogonalCrosshatchFill = "&#x25a6;";
  public static final String squareWithUpperLeftToLowerRightFill = "&#x25a7;";
  public static final String squareWithUpperRightToLowerLeftFill = "&#x25a8;";
  public static final String squareWithDiagonalCrosshatchFill = "&#x25a9;";
  public static final String blackSmallSquare = "&#x25aa;";
  public static final String whiteSmallSquare = "&#x25ab;";
  public static final String blackRectangle = "&#x25ac;";
  public static final String whiteRectangle = "&#x25ad;";
  public static final String blackVerticalRectangle = "&#x25ae;";
  public static final String whiteVerticalRectangle = "&#x25af;";
  public static final String blackParallelogram = "&#x25b0;";
  public static final String whiteParallelogram = "&#x25b1;";
  public static final String blackUppointingTriangle = "&#x25b2;";
  public static final String whiteUppointingTriangle = "&#x25b3;";
  public static final String blackUppointingSmallTriangle = "&#x25b4;";
  public static final String whiteUppointingSmallTriangle = "&#x25b5;";
  public static final String blackRightpointingTriangle = "&#x25b6;";
  public static final String whiteRightpointingTriangle = "&#x25b7;";
  public static final String blackRightpointingSmallTriangle = "&#x25b8;";
  public static final String whiteRightpointingSmallTriangle = "&#x25b9;";
  public static final String blackRightpointingPointer = "&#x25ba;";
  public static final String whiteRightpointingPointer = "&#x25bb;";
  public static final String blackDownpointingTriangle = "&#x25bc;";
  public static final String whiteDownpointingTriangle = "&#x25bd;";
  public static final String blackDownpointingSmallTriangle = "&#x25be;";
  public static final String whiteDownpointingSmallTriangle = "&#x25bf;";
  public static final String blackLeftpointingTriangle = "&#x25c0;";
  public static final String whiteLeftpointingTriangle = "&#x25c1;";
  public static final String blackLeftpointingSmallTriangle = "&#x25c2;";
  public static final String whiteLeftpointingSmallTriangle = "&#x25c3;";
  public static final String blackLeftpointingPointer = "&#x25c4;";
  public static final String whiteLeftpointingPointer = "&#x25c5;";
  public static final String blackDiamond = "&#x25c6;";
  public static final String whiteDiamond = "&#x25c7;";
  public static final String whiteDiamondContainingBlackSmallDiamond = "&#x25c8;";
  public static final String fisheye = "&#x25c9;";
  public static final String lozenge = "&#x25ca;";
  public static final String whiteCircle = "&#x25cb;";
  public static final String dottedCircle = "&#x25cc;";
  public static final String circleWithVerticalFill = "&#x25cd;";
  public static final String bullseye = "&#x25ce;";
  public static final String blackCircle = "&#x25cf;";
  public static final String circleWithLeftHalfBlack = "&#x25d0;";
  public static final String circleWithRightHalfBlack = "&#x25d1;";
  public static final String circleWithLowerHalfBlack = "&#x25d2;";
  public static final String circleWithUpperHalfBlack = "&#x25d3;";
  public static final String circleWithUpperRightQuadrantBlack = "&#x25d4;";
  public static final String circleWithAllButUpperLeftQuadrantBlack = "&#x25d5;";
  public static final String leftHalfBlackCircle = "&#x25d6;";
  public static final String rightHalfBlackCircle = "&#x25d7;";
  public static final String inverseBullet = "&#x25d8;";
  public static final String inverseWhiteCircle = "&#x25d9;";
  public static final String upperHalfInverseWhiteCircle = "&#x25da;";
  public static final String lowerHalfInverseWhiteCircle = "&#x25db;";
  public static final String upperLeftQuadrantCircularArc = "&#x25dc;";
  public static final String upperRightQuadrantCircularArc = "&#x25dd;";
  public static final String lowerRightQuadrantCircularArc = "&#x25de;";
  public static final String lowerLeftQuadrantCircularArc = "&#x25df;";
  public static final String upperHalfCircle = "&#x25e0;";
  public static final String lowerHalfCircle = "&#x25e1;";
  public static final String blackLowerRightTriangle = "&#x25e2;";
  public static final String blackLowerLeftTriangle = "&#x25e3;";
  public static final String blackUpperLeftTriangle = "&#x25e4;";
  public static final String blackUpperRightTriangle = "&#x25e5;";
  public static final String whiteBullet = "&#x25e6;";
  public static final String squareWithLeftHalfBlack = "&#x25e7;";
  public static final String squareWithRightHalfBlack = "&#x25e8;";
  public static final String squareWithUpperLeftDiagonalHalfBlack = "&#x25e9;";
  public static final String squareWithLowerRightDiagonalHalfBlack = "&#x25ea;";
  public static final String whiteSquareWithVerticalBisectingLine = "&#x25eb;";
  public static final String whiteUppointingTriangleWithDot = "&#x25ec;";
  public static final String uppointingTriangleWithLeftHalfBlack = "&#x25ed;";
  public static final String uppointingTriangleWithRightHalfBlack = "&#x25ee;";
  public static final String largeCircle = "&#x25ef;";
  public static final String whiteSquareWithUpperLeftQuadrant = "&#x25f0;";
  public static final String whiteSquareWithLowerLeftQuadrant = "&#x25f1;";
  public static final String whiteSquareWithLowerRightQuadrant = "&#x25f2;";
  public static final String whiteSquareWithUpperRightQuadrant = "&#x25f3;";
  public static final String whiteCircleWithUpperLeftQuadrant = "&#x25f4;";
  public static final String whiteCircleWithLowerLeftQuadrant = "&#x25f5;";
  public static final String whiteCircleWithLowerRightQuadrant = "&#x25f6;";
  public static final String whiteCircleWithUpperRightQuadrant = "&#x25f7;";
  
  // our handy tables
  private Hashtable<Character, String> transTable;
  private Hashtable<Character, String> codeTable;
  
  
  
  /**
   * Creates a new UniBlox instance;
   */
  public UniBlox() {
    this.transTable = new Hashtable<Character, String>();
    this.codeTable = new Hashtable<Character, String>();
  }

  /**
   * Gets a value from the transition table corresponding to the supplied key.
   * @param key   a single-character String
   * @return      value corresponding to the key
   */
  public String get(Character key) {
	  if (transTable.containsKey(key))
		  return transTable.get(key);
	  return key.toString();
  }

  /**
   * Loads a key and its corresponding value into the transition table.
   * @param key     a single-character String
   * @param value   the String value associated with the key
   */
  public void put(Character key, String value) {
    transTable.put(key, value);
  }

  /**
   * Gets a value from the code table corresponding to the supplied key.
   * @param key   a single-character String
   * @return      value corresponding to the key
   */
  public String decode(Character key) {
    return codeTable.get(key);
  }

  /**
   * Loads a key and its corresponding value into the code table.
   * @param key     a single-character String
   * @param value   the String value associated with the key
   */
  public void encode(Character key, String value) {
    codeTable.put(key, value);
  }
}
