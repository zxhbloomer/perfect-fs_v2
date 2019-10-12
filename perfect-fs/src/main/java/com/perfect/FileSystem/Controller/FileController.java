package com.perfect.filesystem.Controller;

import com.perfect.filesystem.Entity.Diskfile;
import com.perfect.filesystem.Entity.DiskfileRepository;
import com.perfect.filesystem.Entity.PagerModel;
import com.perfect.filesystem.Propert.StorageProperties;
import com.perfect.filesystem.Utils.DebugUtil;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(path = "/files")
public class FileController {
	
	private static final int BUTTONS_TO_SHOW = 7;
	private static final int INITIAL_PAGE = 0;
	private static final int INITIAL_PAGE_SIZE = 10;
	private static final int[] PAGE_SIZES = { 5,10,20,50};
	
	@Autowired
	private DiskfileRepository repository;
	@Autowired
	private StorageProperties prop;
	
	@ApiOperation(value="文件系统首页")
	@GetMapping("/index")
	public String index(ModelMap model){
			
			return "file/"+prop.getTemplate()+"/index";
	}

	@ApiOperation(value="文件系统正在建设页")
	@GetMapping("/empty")
	public String emptypage(ModelMap model){
			
			return "file/"+prop.getTemplate()+"/empty";
	}
	
	@ApiOperation(value="全部文件的列表，服务器端自动分页")
	@GetMapping("/all")
	public String list(ModelMap model, Pageable pageable){

		model.addAttribute("page", repository.findAll(pageable));
		
		return "file/"+prop.getTemplate()+"/allfilebyauto";
	}
	
	@ApiOperation(value="全部文件的列表，服务器端手动分页，停用")
	@GetMapping("/all_ManualPaging")
	public ModelAndView getAll(@RequestParam("pageSize") Optional<Integer> pageSize,
			@RequestParam("page") Optional<Integer> page){
				
		ModelAndView modelAndView = new ModelAndView("file/"+prop.getTemplate()+"/allfile");

		int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
		int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;
		DebugUtil.debug("evalPage:"+evalPage);
		PageRequest pageRequest = PageRequest.of(evalPage, evalPageSize);
		
		//PageRequest pageRequest = PageRequest.of(evalPage, evalPageSize, Sort.by(Sort.Direction.ASC,"fileid"))				
		Page<Diskfile> filelist = repository.findAll(pageRequest);
		PagerModel pager = new PagerModel(filelist.getTotalPages(),filelist.getNumber(),BUTTONS_TO_SHOW);

		modelAndView.addObject("filelist",filelist);

		modelAndView.addObject("selectedPageSize", evalPageSize);
		modelAndView.addObject("pageSizes", PAGE_SIZES);
		modelAndView.addObject("pager", pager);

		return modelAndView;
		
	}
		
	@ApiOperation(value="全部文件的列表，带排序分页")	
	@RequestMapping(value = "/findAllSort", method=RequestMethod.GET)
	public Page<Diskfile> getEntryByPageable(@PageableDefault(value = 15, sort = { "fileid" }, direction = Direction.DESC)
	    Pageable pageable) {
	    return repository.findAll(pageable);
	}

}
