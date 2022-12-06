from abc import *
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.alert import Alert
import chromedriver_autoinstaller
import os
import time

class DocumentCrawler():

    def downloadDocument(self, documentList):
        filePathListPerSubject = {}

        if self.__isLoggedIn() == False:
            if(self.validAccount() == False):
                print("invalid account")
                return

            self.__login(self.id, self.pw)

        subjectNames = self.__getSubjectNameList()

        for subjectName in subjectNames:
            self.__accessSubjectPage(subjectName)
            list = self.downloadDocumentByCrawling(documentList)
            if list:
                filePathListPerSubject[subjectName] = list
            self.driver.get('https://ecampus.konkuk.ac.kr/ilos/main/member/login_form.acl')
        return filePathListPerSubject
    
    def installChromeDriver(self):
        chrome_ver = chromedriver_autoinstaller.get_chrome_version().split('.')[0]
        driver_path = f'./{chrome_ver}/chromedriver.exe'
        if os.path.exists(driver_path):
            print(f"chrom driver is insatlled: {driver_path}")
        else:
            print(f"install the chrome driver(ver: {chrome_ver})")
            chromedriver_autoinstaller.install(True)
        self.driver = webdriver.Chrome(driver_path)
        self.driver_path = driver_path
        return

    def __enterLoginInfo(self):
        self.driver.find_element(By.ID,'usr_id').send_keys(self.id)
        self.driver.find_element(By.ID,'usr_pwd').send_keys(self.pw)
        try:
            self.driver.find_element(By.ID, 'login_btn').click()
        except:
            return


    def validAccount(self, id, pw):
        self.id = id
        self.pw = pw

        self.installChromeDriver()
        # return True
        loginURL = 'https://ecampus.konkuk.ac.kr/ilos/main/member/login_form.acl'
        self.driver.get(loginURL)

        # self.driver.implicitly_wait(10)

        self.__enterLoginInfo()

        # TODO: url에 의하여 판단하는 것으로 기준 변경
        if(self.driver.current_url == loginURL):
            print('login failed')
            return False
        else:
            print('login success')
            return True

    def __getSubjectNameList(self):
        subjectNames = []
        subjects = self.driver.find_elements(By.CSS_SELECTOR, '.term_info ~ li')
        for subject in subjects:
            if(subject.get_attribute('class') != "term_info"):
                rawName = subject.find_element(By.CLASS_NAME, 'sub_open').text
                subjectNames.append(rawName[rawName.find(']')+1 : rawName.rfind('(')])
            else: 
                break
        return subjectNames

    def __isLoggedIn(self):
        if "수강과목" in self.driver.page_source:
            print('already logged in')
            return True
        else:
            print('not logged in -> trying to login...')
            return False

    def getDocumentList(self):
        documentList = {}
        subjectNames = []

        if self.__isLoggedIn() == False:
            if(self.validAccount(self.id, self.pw) == False):
                print("invalid account")
                return

            # self.driver.implicitly_wait(1)

            self.__login(self.id, self.pw)

        subjectNames = self.__getSubjectNameList()

        for subjectName in subjectNames:
            self.__accessSubjectPage(subjectName)
            list = self.createDocumentListByCrawling()
            if list:
                documentList[subjectName] = list
            self.driver.get('https://ecampus.konkuk.ac.kr/ilos/main/member/login_form.acl')
        
        return documentList
    
    

    def __login(self, id, pw):
        self.driver.get('https://ecampus.konkuk.ac.kr/ilos/main/member/login_form.acl')
        self.driver.find_element(By.ID,'usr_id').send_keys(id)
        self.driver.find_element(By.ID,'usr_pwd').send_keys(pw)
        self.driver.find_element(By.ID, 'login_btn').click()

        while "수강과목" not in self.driver.page_source:
            time.sleep(1)
        return

    def __accessSubjectPage(self, subjectName):
        subjects = self.driver.find_elements(By.CLASS_NAME, 'sub_open')
        for subject in subjects:
            if(subject.text.find(subjectName) != -1):
                classNum = subject.get_attribute('kj')
                self.driver.execute_script('eclassRoom'+"('"+classNum+"')")
                break
        return  
    
    @abstractmethod
    def createDocumentListByCrawling(self):
        pass

    @abstractmethod
    def downloadDocumentByCrawling(self):
        pass

