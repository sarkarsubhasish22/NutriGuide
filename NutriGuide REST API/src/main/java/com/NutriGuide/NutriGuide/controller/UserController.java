package com.NutriGuide.NutriGuide.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.NutriGuide.NutriGuide.dao.BMIDao;
import com.NutriGuide.NutriGuide.dao.CCDao;
import com.NutriGuide.NutriGuide.dao.FarmerDao;
import com.NutriGuide.NutriGuide.dao.MDDDao;
import com.NutriGuide.NutriGuide.dao.MUACDao;
import com.NutriGuide.NutriGuide.dao.PFIDao;
import com.NutriGuide.NutriGuide.dao.SFTDao;
import com.NutriGuide.NutriGuide.dao.VO2Dao;
import com.NutriGuide.NutriGuide.entities.BMI;
import com.NutriGuide.NutriGuide.entities.CC;
import com.NutriGuide.NutriGuide.entities.Farmer;
import com.NutriGuide.NutriGuide.entities.MDD;
import com.NutriGuide.NutriGuide.entities.MUAC;
import com.NutriGuide.NutriGuide.entities.PFI;
import com.NutriGuide.NutriGuide.entities.SFT;
import com.NutriGuide.NutriGuide.entities.Status;
import com.NutriGuide.NutriGuide.entities.User;
import com.NutriGuide.NutriGuide.entities.VO2;
import com.NutriGuide.NutriGuide.services.BMIService;
import com.NutriGuide.NutriGuide.services.CCService;
import com.NutriGuide.NutriGuide.services.FarmerService;
import com.NutriGuide.NutriGuide.services.MDDService;
import com.NutriGuide.NutriGuide.services.MUACService;
import com.NutriGuide.NutriGuide.services.PFIService;
import com.NutriGuide.NutriGuide.services.SFTService;
import com.NutriGuide.NutriGuide.services.UserService;
import com.NutriGuide.NutriGuide.services.VO2Service;

@RestController
public class UserController {
	
	@GetMapping("/home")
	public String home() {
		return "goddamn";
	}
	
//	controller for user table
	
	@Autowired
	private UserService userService;
	
	
	@GetMapping("/user")
	public List<User> getUser() {
		return userService.getUser();
	}
	
	@PostMapping("/users/register")
    public User registerUser(@Validated @RequestBody User newUser) {
		
		return this.userService.registerUser(newUser);
		
	}
	

	@PostMapping("/users/login")
    public User loginUser(@Validated @RequestBody User user) {
		
		return this.userService.loginUser(user);
		
	}
	
	
	
	
//	controller for farmer table
	
	@Autowired
	private FarmerService farmerService;
	
	@GetMapping("/user/farmers")
	public List<Farmer> getFarmer(){
		
		return this.farmerService.getFarmer();
	}
	
	@PostMapping("/user/farmer")
	public Farmer addFarmer(@RequestBody Farmer farmer) {

		return this.farmerService.addFarmer(farmer);
		
	}
	
	@GetMapping("/user/farmers/{userId}")
	public List<Farmer> getFarmerByuserId(@PathVariable long userId){
		return this.farmerService.getFarmerByuserId(userId);
		
	}
	
	@Autowired
	private FarmerDao farmerDao;
	
	@GetMapping("/user/farmer/{farmerId}")
	public Farmer getFarmerByfarmerId(@PathVariable long farmerId){
		return this.farmerDao.getFarmerByfarmerId(farmerId);
		
	}
	
	
	//Controller for PFI table
	
	@Autowired
	private PFIService pfiService;
	
	@PostMapping("/user/farmer/PFI")
	public PFI addPFI(@RequestBody PFI pfi){
		
		return this.pfiService.addPFI(pfi);
	}
	
	@Autowired
	private PFIDao pfiDao;
	
	@GetMapping("/user/farmer/PFI/{farmerId}")
	public PFI getPFIByfarmerId(@PathVariable long farmerId){
		return this.pfiDao.getPFIByfarmerId(farmerId);
		
	}
	
	//Controller for VO2 table
	
	@Autowired
	private VO2Service vo2Service;
	
	@PostMapping("/user/farmer/VO2")
	public VO2 addVO2(@RequestBody VO2 vo2) {
		
		return this.vo2Service.addVO2(vo2);
		
	}
	
	@Autowired
	private VO2Dao vo2Dao;
	
	@GetMapping("/user/farmer/VO2/{farmerId}")
	public VO2 getVO2ByfarmerId(@PathVariable long farmerId){
		return this.vo2Dao.getVO2ByfarmerId(farmerId);
		
	}
	
	
	//Controller for BMI table
	
	@Autowired
	private BMIService bmiService;
	
	@PostMapping("/user/farmer/BMI")
	public BMI addBMI(@RequestBody BMI bmi) {
		
		return this.bmiService.addBMI(bmi);
		
	}
	
	@Autowired
	private BMIDao bmiDao;
	
	@GetMapping("/user/farmer/BMI/{farmerId}")
	public BMI getBMIByfarmerId(@PathVariable long farmerId){
		return this.bmiDao.getBMIByfarmerId(farmerId);
		
	}
	
	//Controller for MUAC table
	
	@Autowired
	private MUACService muacService;
	
	@PostMapping("/user/farmer/MUAC")
	public MUAC addMUAC(@RequestBody MUAC muac) {
		
		return this.muacService.addMUAC(muac);
		
	}
	
	@Autowired
	private MUACDao muacDao;
	
	@GetMapping("/user/farmer/MUAC/{farmerId}")
	public MUAC getMUACByfarmerId(@PathVariable long farmerId){
		return this.muacDao.getMUACByfarmerId(farmerId);
		
	}
	
	//Controller for CC table
	
	@Autowired
	private CCService ccService;
	
	@PostMapping("/user/farmer/CC")
	public CC addCC(@RequestBody CC cc) {
		return this.ccService.addCC(cc);
		
	}
	
	@Autowired
	private CCDao ccDao;
	
	@GetMapping("/user/farmer/CC/{farmerId}")
	public CC getCCByfarmerId(@PathVariable long farmerId){
		return this.ccDao.getCCByfarmerId(farmerId);
		
	}
	
	
	//Controller for SFT table
	
	@Autowired
	private SFTService sftService;
	
	@PostMapping("/user/farmer/SFT")
	public SFT addSFT(@RequestBody SFT sft) {
		
		return this.sftService.addSFT(sft);
		
	}
	
	@Autowired
	private SFTDao sftDao;
	
	@GetMapping("/user/farmer/SFT/{farmerId}")
	public SFT getSFTByfarmerId(@PathVariable long farmerId){
		return this.sftDao.getSFTByfarmerId(farmerId);
		
	}
	
	//Controller for MDD table
	
	@Autowired
	private MDDService mddService;
	
	@PostMapping("/user/farmer/MDD")
	public MDD addMDD(@RequestBody MDD mdd) {
		
		return this.mddService.addMDD(mdd);
	}
	
	@Autowired
	private MDDDao mddDao;
	
	@GetMapping("/user/farmer/MDD/{farmerId}")
	public MDD getMDDByfarmerId(@PathVariable long farmerId){
		return this.mddDao.getMDDByfarmerId(farmerId);
		
	}
}











