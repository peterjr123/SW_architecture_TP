import getpass
import json
from selenium import webdriver

'''
건국대학교 e캠퍼스 자동로그인  
'''

options = webdriver.ChromeOptions()
crawl_api = 'http://ecampus.konkuk.ac.kr/ilos/main/main_form.acl'
# headless 옵션 설정 : 개발환경이 리눅스라면 아래 두가지는 포함
options.add_argument('headless')
options.add_argument("no-sandbox")

# 브라우저 사이즈 : 현재 창을 열지 않는 방식으로 구현
# options.add_argument('window-size=800,600')

user_id = input('아이디를 입력하세요: ')
user_pw = getpass.getpass('비밀번호를 입력하세요: ')

# 드라이버 위치 경로 입력
driver = webdriver.Chrome()

# url을 이용하여 브라우저로 접속
driver.get('https://ecampus.konkuk.ac.kr/ilos/main/member/login_form.acl')

# 대기시간 부여
driver.implicitly_wait(3)

driver.find_element_by_id('usr_id').send_keys(user_id)
driver.find_element_by_id('usr_pwd').send_keys(user_pw)
driver.find_element_by_xpath('//*[@id="login_btn"]').click()

# 대기시간 부여
driver.implicitly_wait(5)

_cookies = driver.get_cookies()
cookie_dict = {}
for cookie in _cookies:
    cookie_dict[cookie['name']] = cookie['value']

session = requests.Session()
headers = {'User-Agent': 'Mozila/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36(KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36 Edg/96.0.1054.62'}

session.headers.update(headers)
session.cookies.update(cookie_dict)
headers = {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}

res = session.post(crawl_api, headers=headers, data=data)
json_obj = json.loads(res.text)
print(json_obj)

#driver.quit()
