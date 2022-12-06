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
        if(self.validAccount(self.id, self.pw) == False):
            print("invalid account")
            return

        self.__login(self.id, self.pw)

        subjectNames = self.__getSubjectNameList()

        for subjectName in subjectNames:
            self.__accessSubjectPage(subjectName)
            filePathListPerSubject[subjectName] = self.downloadDocumentByCrawling(documentList)
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
        return

    def __enterLoginInfo(self):
        self.driver.find_element(By.ID,'usr_id').send_keys(self.id)
        self.driver.find_element(By.ID,'usr_pwd').send_keys(self.pw)
        self.driver.find_element(By.ID, 'login_btn').click()

    def validAccount(self):
        return True
        # loginURL = 'https://ecampus.konkuk.ac.kr/ilos/main/member/login_form.acl'
        # self.driver.get(loginURL)
        # self.__enterLoginInfo()

        # try: 
        #     Alert(self.driver).accept()
        #     print('login failed')
        #     self.driver.close()
        #     return True
        # except:
        #     print('login success')
        #     self.driver.close()
        #     return False

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

    def getDocumentList(self):
        documentList = {}
        subjectNames = []
        if(self.validAccount() == False):
            print("invalid account")
            return

        self.__login(self.id, self.pw)

        subjectNames = self.__getSubjectNameList()

        for subjectName in subjectNames:
            self.__accessSubjectPage(subjectName)
            documentList[subjectName] = self.createDocumentListByCrawling()
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

