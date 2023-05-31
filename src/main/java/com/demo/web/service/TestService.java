package com.demo.web.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.web.api.SnowApi;
import com.demo.web.dto.SnowApiRequestDto;
import com.demo.web.dto.TestResultDto;
import com.demo.web.entity.TestRequestEntity;
import com.demo.web.entity.TestResultEntity;
import com.demo.web.form.TestForm;
import com.demo.web.repository.TestRequestRepository;
import com.demo.web.repository.TestResultRepository;

@Service
public class TestService {

	@Autowired
	TestRequestRepository testRequestRepository;
	
	@Autowired
	TestResultRepository testResultRepository;
	
	@Autowired
	SnowApi snowApi;

	private final int SNOW_SUCCESS_CODE = 201;
	
	public int testRequest(TestForm testForm) {
		System.out.println(testForm.getFaultDevice() + ":" + testForm.getContractType());
		TestRequestEntity testRequestEntity = new TestRequestEntity();
		testRequestEntity.setFaultDevice(testForm.getFaultDevice());
		testRequestEntity.setContractType(testForm.getContractType());
		testRequestRepository.save(testRequestEntity);

		if (testForm.getFaultDevice() == "" || testForm.getContractType() == "") {
			//System.out.println("fail");
			return 0;
		} else {
			//System.out.println("success");
			return testRequestEntity.getTicketNumber();
		}
	}
	
	

	
	
	public TestResultDto testResult(int ticketNumber) {

		// チケット番号をキーに試験依頼テーブルからレコード取得
		Optional<TestRequestEntity> optinalTestRequestEntity = testRequestRepository.findById(ticketNumber);
		TestRequestEntity testRequestEntity = optinalTestRequestEntity.get();

		// 試験結果テーブルに登録する用のエンティティ生成
		TestResultEntity testResultEntity = new TestResultEntity();
		testResultEntity.setTicketNumber(testRequestEntity.getTicketNumber());
		testResultEntity.setContractType(testRequestEntity.getContractType());
		testResultEntity.setFaultDevice(testRequestEntity.getFaultDevice());

		// 契約タイプから試験結果を判定
		switch (testRequestEntity.getContractType()) {
		case "契約タイプA":
			testResultEntity.setResult("正常");
			break;
		case "契約タイプB":
			testResultEntity.setResult("異常");
			break;
		// 一致しなかった場合
		default:
			testResultEntity.setResult("異常");
		}

		// 判定後、試験結果テーブルにレコード登録する
		testResultRepository.save(testResultEntity);

		// SNOWに連携する
		SnowApiRequestDto sNOWApiRequestDto = new SnowApiRequestDto();
		sNOWApiRequestDto.setTicketNumber(testResultEntity.getTicketNumber());
		sNOWApiRequestDto.setFaultDevice(testResultEntity.getFaultDevice());
		sNOWApiRequestDto.setContractType(testResultEntity.getContractType());
		sNOWApiRequestDto.setResult(testResultEntity.getResult());

		// SNOW連携結果の成功・失敗を判定
		int statusCode = snowApi.execSNOWApi(sNOWApiRequestDto);

		// 画面に返す用のDTO
		TestResultDto testResultDto = new TestResultDto();

		// 連携成功した場合、DTOにデータを詰めて返却
		if (statusCode == SNOW_SUCCESS_CODE) {
			testResultDto.setTicketNumber(testResultEntity.getTicketNumber());
			testResultDto.setContractType(testResultEntity.getContractType());
			testResultDto.setFaultDevice(testResultEntity.getFaultDevice());
			testResultDto.setResult(testResultEntity.getResult());
		}

		return testResultDto;

	}
}
