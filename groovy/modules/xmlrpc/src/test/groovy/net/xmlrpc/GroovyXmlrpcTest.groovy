/*
 * Copyright 2004 (C) John Wilson. All Rights Reserved.
 * 
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met: 1. Redistributions of source code must retain
 * copyright statements and notices. Redistributions must also contain a copy
 * of this document. 2. Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the distribution. 3.
 * The name "groovy" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Codehaus. For
 * written permission, please contact info@codehaus.org. 4. Products derived
 * from this Software may not be called "groovy" nor may "groovy" appear in
 * their names without prior written permission of The Codehaus. "groovy" is a
 * registered trademark of The Codehaus. 5. Due credit should be given to The
 * Codehaus - http://groovy.codehaus.org/
 * 
 * THIS SOFTWARE IS PROVIDED BY THE CODEHAUS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE CODEHAUS OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 *  
 */

package groovy.net.xmlrpc

import java.net.ServerSocket

import groovy.util.GroovyTestCase

/**
 * Tests the use of the structured Attribute type
 * 
 * @author <a href="mailto:tug@wilson.co.uk">John Wilson/a>

 */
public class GroovyXmlrpcTest extends GroovyTestCase {

    public void testXmlrpcCalls() {
		//
		// create a new XML-RPC server
		//
		server = new XMLRPCServer(2, 10, 8, 1000, 1000)
		
		//
		// add methods that can be called remotely
		//
		server.validator1.arrayOfStructsTest = {structs |      
							                   		count = 0
						                   		
							                   		for (struct in structs) {
							                   			count += struct['curly']
							                   		}
							                   		
							                   		return count
							                   }
		
		server.validator1.countTheEntities = {string |      
						                   		ctLeftAngleBrackets = 0
						                   		ctRightAngleBrackets = 0
						                   		ctAmpersands = 0
						                   		ctApostrophes = 0
						                   		ctQuotes = 0
						                   		 
						                   		for (c in string) {
						                   			switch (c) {
						                   				case '<' :
						                   					ctLeftAngleBrackets++
						                   					break;
						                   					
						                   				case '>' :
						                   					ctRightAngleBrackets++
						                   					break;
						                   					
						                   				case '&' :
						                   					ctAmpersands++
						                   					break;
						                   					
						                   				case '\'' :
						                   					ctApostrophes++
						                   					break;
						                   					
						                   				case '"' :
						                   					ctQuotes++
						                   					break;
						                   					
						                   				default:
						                   			}
						                   		}
						                   		
						                   		return ['ctLeftAngleBrackets' : ctL�d�Ӻ1���(�!L	S*�J���[�Q98ۇ�,���	��ú�(�eRJgw/H�+Nɛ�+�وc(�}���βzQ�N�Vc��!{7�l=�$�
MϠ~*}1��
�`|�����B�����b�4D,5g�nyo[>��f�̆T9Lym�(�uj���`K���䵉�ODtSA���F�)��fU���y+ת=Dک-��,E���0�ȁ�!'@$�P4t�+uڎ�y�)��$���Y�l�.�F�S���[�'>t�I�(�����ہr��uf�)��g�Y�X�ۍ�`C^��R}6���Ðp:M<h o�烦qK��=%D��U�`܇����,��Xq���
��;��u�J��jo�}
�]��Z�_t,����n�ޘʴ+���z<3��Mui���~ϼ�h���<q�2�W��u�.�X%�D%�'Bv0'�#�pO�i�ȇ���8�Q$l8o�x:�f)�.p�JƬ�	�m�^�
�c���-�
���-W�eO �;�\A�i��(�7J�Y��3'4ot�	�r��9w2��^������s��0����f)�d9�ߒ[��هd�a]�b��R�~�?w��qӯ|6�r��-��Hq�.���ތ�6�$�tcyr���bc�jշ���L�Rw�NaS�Lb4������)@c>��I8���4|�<?s����'���v�SlW�#c���X���������������{�)�S������QǄO�E��`� ,�Ң��a)��z�Q��-�묞�\\vP�%㩯��~��\:�p<�|�@H�+���g_5�l�άu\Ĩ2W]=z7)2�L���t�_o׫��F!�P�"�����ϓD�&W����C^������UUnM��q��EG�`
�Р�"�N���i��aL�7�0*;�u�vQn�9��_��Ҧ�O�G�����RN��j ��]�'t^�sE�L3��[�ۧc���7�wH�D�EQA�*����S�o�E��0�Xn5(������Uͣ��e͌�����:��H�g^�')�랋��T�fNm2_Zk^L7r�x`$kv,Rړ��Vu��*��`��oI����k�K���p�B��'J���q�Znk�r4S�+,��b=����{���[��1���{�������:�A>*��`̶c�:��7���~�'Mڲ�݊V�^��%����ͦ�PUs���-�~R�](��eN^4[�lCf��E+��J�u���4�;.n�dL�n�iH����QI�r]Dh���H��zr %ZVz�����B��a~�H������+MO�T�]�K���F�����n��Z��!��}�Ԁ#��mt�;)���ր7��)#�҅Aƪ���&Aǩ�:kGI�*¬8\Z�Fסܬ�v5g��u�؎cS1Y��#�O+a]����C�Y8��Y�W���o�ΐ/LZQ:����Y��Sy��H ��K������T�Q���X�|��=&��@�_|
B�xe������*�o��x6`w��
��Q)�-��Z�kWWpHS3bp^��h�1�ſ���AQ�������s�}*?��B�|N�$B�u��\8�����C���s2T;�;��IO�bj�?���rL鸖��,�l&���n�+����
���{y�ȝ��c�K[������s'�bř�?PA�OP���B���������K�_@�l��Y�x��[Upy= D¦��A|��==|`0�g��V�@*��ʽX�&[��~�l��@�����?@Ao�5T�CI�v�%#F����f������Q�u�U�:�Ꜹ���xc�c.�Ddoc�6�����S	mSUe�)C��֡����L,�?BGyC>�@K�U%�:�)m0���^���D<ϊe`<z����*��8�
��zH��9H� �&�&��T���
�me��P�_�O��ȟ0lޥ&T!�0 ) <�I�L�A1j���!;�����N����7���â�5ȸ��n���Y��6��`��ݚ��v-n�V�`Je��i�|@�c���
��D�G@Oi�FD���t�t���L�-�Ѯt����Ϋ��ʶm����5�4�򚫶F�X&�Pqa(J�7ӈ�F��WX�Ҏ��`�צ�[F�z� �S�R
� 'Ň���Ĳ��#�|��um���8��5�$��>*b�#b���+)C�.�I�H�z?>a�7��7	�P�2���&8Ջb_y���Q����rۗ�����K�1�s�2�uNF�ݛT�Q�,����q�|:~7*8��u�%��l'j�&��T�t`�w
)�=�Nik�
Ht�WZMVDg�`�ʗC��%����)�
�׉���W��0�&�:�d�䗫�=>�
�IjZ:��	Mխ���e�@=�<�<�&`��kq�&�'bb�*��Ȟ�~��[y��!�,��Gr��׮	Ji�֩��f�F�(�_e���: o���Px��D[�t�W�A�MwD�K�VӧA�ǒ�i�:.<8݋�p}:ϮP{��H����Fƌ��Vߡ6"�uH>����_�r�w�m�x�S�=Hܙ�+�SN�`"3X�������RV�)��R)�g`a�k�6�<�R���v��{i�ĝ�λޫ-�L<�
�랬#`�a�?�$�L��9��%0��j�Y�B���
��i�Ja˖���2V�R��EF�b#�=�zs�e����z�R� R&bח>�k'9N�;ߝoP��y�yb��u�w�e�z�&6�)�w,	"�����k'*m�U�,���7�rQ�&��k�b��N���8S�;S����� ��R����)�'T��]�W�
cx����G��	��3=��PS��D��w�'�pN�u�P�gu��"����(