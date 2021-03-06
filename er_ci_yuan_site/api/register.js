import request from '~/utils/request'

export default{
  sendSms(mobile) {
    return request({
      url: `/api/site/sms/send/${mobile}`,
      method: 'get'
    })
  },

  register(member) {
    return request({
      url: `/api/site/member/register`,
      method: 'post',
      data: member
    })
  },
  registerByOauth2(member, id, type) {
    return request({
      url: `/api/site/member/register/${id}/${type}`,
      method: 'post',
      data: member
    })
  }
}
