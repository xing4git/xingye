$('#add_data_modal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget) // Button that triggered the modal
    var zhishuId = button.data('zhishu')
    var modal = $(this)
    modal.find('#zhishuId').val(zhishuId)
})