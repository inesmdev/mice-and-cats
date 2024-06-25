/*
 * Generated by ISE 24.05.10.7822 - linux-x86-64
 */

#include "eif_eiffel.h"
#include "eif_project.h"
#include "egc_include.h"

#ifdef __cplusplus
extern "C" {
#endif
extern void F1_23();
extern EIF_REFERENCE F1_14();
extern void F236_6591();
extern void F238_6767();
extern void F236_6595();
extern void F243_6835();
extern void F244_6976();
extern void F243_6840();
extern void F321_3174();
extern void F279_6472();
extern void F78_1554();
extern EIF_REFERENCE F78_1541();
extern EIF_BOOLEAN F78_1553();
extern EIF_BOOLEAN F78_1561();
extern void F78_1563();
extern void F78_1564();
extern void F78_1565();
extern void F53_1239();
extern EIF_TYPED_VALUE F53_1240();

long *eif_area_table = (long *)0;
long *eif_lower_table = (long *)0;


extern void egc_init_plug (void); 
void egc_init_plug (void)
{
	egc_prof_enabled = (EIF_INTEGER) 0;
	egc_correct_mismatch = (void (*)(EIF_REFERENCE)) F1_23;
	egc_twin = (EIF_TYPED_VALUE (*)(EIF_REFERENCE)) F1_14;
	egc_strmake = (void (*)(EIF_REFERENCE, EIF_TYPED_VALUE)) F236_6591;
	egc_immstr8make_from_c_byte_array = (void (*)(EIF_REFERENCE, EIF_TYPED_VALUE, EIF_TYPED_VALUE)) F236_6595;
	egc_str32make = (void (*)(EIF_REFERENCE, EIF_TYPED_VALUE)) F243_6835;
	egc_immstr32make_from_c_byte_array = (void (*)(EIF_REFERENCE, EIF_TYPED_VALUE, EIF_TYPED_VALUE)) F243_6840;
	egc_arrmake = (void (*)(EIF_REFERENCE, EIF_TYPED_VALUE, EIF_TYPED_VALUE)) F321_3174;
	egc_strset = (void (*)(EIF_REFERENCE, EIF_TYPED_VALUE)) F238_6767;
	egc_str32set = (void (*)(EIF_REFERENCE, EIF_TYPED_VALUE)) F244_6976;
	egc_routdisp_wb = (void (*)(EIF_REFERENCE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE)) F279_6472;
	egc_is_scoop_capable = 1;
	egc_set_exception_data = (void (*)(EIF_REFERENCE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE, EIF_TYPED_VALUE)) F78_1554;
	egc_set_last_exception = (void (*)(EIF_REFERENCE, EIF_TYPED_VALUE)) F78_1553;
	egc_last_exception = (EIF_TYPED_VALUE (*)(EIF_REFERENCE)) F78_1541;
	egc_is_code_ignored = (EIF_TYPED_VALUE (*)(EIF_REFERENCE, EIF_TYPED_VALUE)) F78_1561;
	egc_once_raise = (void (*)(EIF_REFERENCE, EIF_TYPED_VALUE)) F78_1563;
	egc_init_exception_manager = (void (*)(EIF_REFERENCE)) F78_1564;
	egc_free_preallocated_trace = (void (*)(EIF_REFERENCE)) F78_1565;

	egc_str_dtype = 237;

	egc_immstr8_dtype = 246;

	egc_str32_dtype = 243;

	egc_immstr32_dtype = 245;
	egc_arr_dtype = 320;
	egc_tup_dtype = 191;
	egc_disp_rout_id = 3105;
	egc_copy_rout_id = 18;
	egc_is_equal_rout_id = 11;

	egc_sp_char = 0x0305;
	egc_sp_wchar = 0x021D;
	egc_sp_bool = 0x01A1;
	egc_sp_uint8 = 0x0299;
	egc_sp_uint16 = 0x02BD;
	egc_sp_uint32 = 0x025B;
	egc_sp_uint64 = 0x0334;
	egc_sp_int8 = 0x03A8;
	egc_sp_int16 = 0x02E1;
	egc_sp_int32 = 0x01D0;
	egc_sp_int64 = 0x0384;
	egc_sp_real32 = 0x0159;
	egc_sp_real64 = 0x017D;
	egc_sp_pointer = 0x0237;
	egc_sp_ref = 0x013D;

	egc_uint8_dtype = 211;
	egc_uint16_dtype = 220;
	egc_uint32_dtype = 217;
	egc_uint64_dtype = 229;
	egc_int8_dtype = 193;
	egc_int16_dtype = 226;
	egc_int32_dtype = 223;
	egc_int64_dtype = 202;
	egc_bool_dtype = 205;
	egc_real32_dtype = 214;
	egc_char_dtype = 199;
	egc_wchar_dtype = 196;
	egc_real64_dtype = 208;
	egc_point_dtype = 232;

	egc_exception_dtype = 95;
	egc_except_emnger_dtype = 77;

	egc_ce_type = egc_ce_type_init;

	egc_ce_exp_type = egc_ce_exp_type_init;
	egc_fsystem = egc_fsystem_init;
	egc_system_mod_init = egc_system_mod_init_init;
	egc_partab = egc_partab_init;
	egc_partab_size = egc_partab_size_init;
	egc_foption = egc_foption_init;
	egc_frozen = egc_frozen_init;
	egc_fpatidtab = egc_fpatidtab_init;
	egc_address_table = egc_address_table_init;
	egc_fpattern = egc_fpattern_init;

	egc_einit = egc_einit_init;
	egc_tabinit = egc_tabinit_init;
	egc_forg_table = egc_forg_table_init;

	egc_system_name = "cli-cat-mouse";
	egc_system_location = "/home/andrea/Dokumente/TU_Wien/Master_SE/2024SS/VU_FOOP/exercises/ex2test/EIFGENs/cli-cat-mouse/W_code";
	egc_compiler_tag = 39;
	egc_project_version = 1719309867;
	egc_has_old_special_semantic = 0;
	scount = 982;

	egc_rcount = 1;
	egc_ridx = 0;
	egc_rlist = (char**) eif_malloc (sizeof(char*)*egc_rcount);
	egc_rcdt = (int32 *) eif_malloc (sizeof(int32)*egc_rcount);
	egc_rcrid = (int32 *) eif_malloc (sizeof(int32)*egc_rcount);
	egc_rcarg = (int32 *) eif_malloc (sizeof(int32)*egc_rcount);
	egc_rlist[0] = "APPLICATION.make";
	egc_rcdt[0] = 0;
	egc_rcrid[0] = 5168;
	egc_rcarg[0] = 0;
	
	
	egc_platform_level = 0x00000D00;
	egc_rt_extension_dt = 52;
	egc_rt_extension_notify = (void (*)(EIF_REFERENCE, EIF_TYPED_VALUE, EIF_TYPED_VALUE)) F53_1239;
	egc_rt_extension_notify_argument = (EIF_TYPED_VALUE (*)(EIF_REFERENCE, EIF_TYPED_VALUE)) F53_1240;
}

void egc_rcdt_init (void)
{
	if (egc_rcdt[0] == 0) {
		egc_rcdt[0] = 981; /* APPLICATION */
	}
}

#ifdef __cplusplus
}
#endif
